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

import me.mrabar.discrepancy.data.DiscrepancyReport;
import me.mrabar.discrepancy.utils.DependencyUtils;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.project.ProjectBuildingRequest;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class Analyzer {
  private final Log log;
  private final MavenSession session;
  private final ProjectBuilder builder;
  private final Map<String, Dependency> mainDependencies;
  private final List<File> poms;

  private final ExecutorService executor;

  public Analyzer(
      Log log,
      MavenSession session,
      ProjectBuilder builder,
      Map<String, Dependency> mainDependencies,
      List<File> poms,
      int threads
  ) {
    this.log = log;
    this.session = session;
    this.builder = builder;
    this.mainDependencies = mainDependencies;
    this.poms = poms;
    executor = Executors.newFixedThreadPool(threads);
  }

  public List<DiscrepancyReport> analyze() {
    ProjectBuildingRequest request = session.getProjectBuildingRequest();

    return poms.stream()
        .map(pom -> executor.submit(new CallablePomInspector(log, pom, builder, request, mainDependencies)))
        .map(this::getReport)
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }

  private DiscrepancyReport getReport(Future<DiscrepancyReport> f) {
    try {
      return f.get();
    } catch (InterruptedException | ExecutionException e) {
      throw new RuntimeException(e);
    }
  }

}
