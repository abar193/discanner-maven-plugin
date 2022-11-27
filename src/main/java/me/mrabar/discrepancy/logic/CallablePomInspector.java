/*
 * Copyright 2022 Anton Bardishev
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package me.mrabar.discrepancy.logic;

import me.mrabar.discrepancy.data.Discrepancy;
import me.mrabar.discrepancy.data.DiscrepancyReport;
import me.mrabar.discrepancy.utils.DependencyUtils;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.*;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

public class CallablePomInspector implements Callable<DiscrepancyReport> {
  private final Log log;
  private final File pom;
  private final ProjectBuilder builder;
  private final ProjectBuildingRequest projectBuildingRequest;
  private final Map<String, Dependency> dependencies;

  public CallablePomInspector(
      Log log,
      File pom,
      ProjectBuilder builder,
      ProjectBuildingRequest projectBuildingRequest,
      Map<String, Dependency> dependencies
  ) {
    this.log = log;
    this.pom = pom;
    this.builder = builder;
    this.projectBuildingRequest = projectBuildingRequest;
    this.dependencies = dependencies;
  }

  @Override
  public DiscrepancyReport call() throws Exception {
    ProjectBuildingResult result = buildProject(pom, builder, projectBuildingRequest);

    if (result == null || result.getProject() == null) {
      return null;
    }

    MavenProject project = result.getProject();

    if (!DependencyUtils.isIncluded(project, dependencies)) {
      return null;
    }

    List<Discrepancy> discrepancies = project.getDependencies()
        .stream()
        .map(d -> DependencyUtils.checkDiscrepancy(d, dependencies))
        .filter(Objects::nonNull)
        .collect(Collectors.toList());

    return new DiscrepancyReport(
        project.getGroupId(),
        project.getArtifactId(),
        project.getName(),
        discrepancies
    );
  }

  private ProjectBuildingResult buildProject(
      File pom,
      ProjectBuilder builder,
      ProjectBuildingRequest projectBuildingRequest
  ) {
    try {
      ProjectBuildingRequest prb = new DefaultProjectBuildingRequest(projectBuildingRequest);
      return builder.build(pom, prb);
    } catch (ProjectBuildingException e) {
      log.error(e);
      return null;
    }
  }
}
