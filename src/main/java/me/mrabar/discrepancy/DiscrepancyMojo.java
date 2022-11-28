/*
 * Copyright 2022 Anton Bardishev
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package me.mrabar.discrepancy;

import me.mrabar.discrepancy.data.Discrepancy;
import me.mrabar.discrepancy.data.DiscrepancyReport;
import me.mrabar.discrepancy.logic.Analyzer;
import me.mrabar.discrepancy.utils.CsvWriter;
import me.mrabar.discrepancy.utils.DependencyUtils;
import me.mrabar.discrepancy.utils.ProjectUtils;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;

import java.io.File;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Mojo(
    name = "scan",
    requiresDependencyResolution = ResolutionScope.COMPILE
)
public class DiscrepancyMojo extends AbstractMojo {
  @Component
  private ProjectBuilder mavenProjectBuilder;

  @Parameter(defaultValue = "${session}", required = true, readonly = true)
  private MavenSession session;

  @Parameter(property = "threads", defaultValue = "1")
  private int threads;

  @Parameter(property = "projectScanDir")
  private String projectScanDir;

  @Parameter(property = "csvReportLocation")
  private String csvReportLocation;

  public void execute() throws MojoExecutionException, MojoFailureException {
    MavenProject mainProject = (MavenProject) getPluginContext().get("project");
    List<File> poms = ProjectUtils.scanProjects(getLog(), projectScanDir);

    Map<String, Dependency> dependencies = DependencyUtils.dependencyMap(mainProject.getDependencies());

    if(dependencies.size() == 0) {
      getLog().info(String.format("No dependencies found on project %s, nothing to do", mainProject.getName()));
      return;
    }

    Analyzer a = new Analyzer(getLog(), session, mavenProjectBuilder, dependencies, poms, threads);
    List<DiscrepancyReport> reports = a.analyze();

    reports.sort(Comparator.comparing(DiscrepancyReport::getProjectName));

    for (DiscrepancyReport report : a.analyze()) {
      reportDiscrepancies(report);
    }

    if (csvReportLocation != null) {
      CsvWriter.reportCsv(csvReportLocation, reports);
    }
  }

  private void reportDiscrepancies(DiscrepancyReport report) {
    if (report.getDiscrepancies().isEmpty()) {
      return;
    }

    getLog().info(String.format(
        "Discrepancies in %s:%s (%s)",
        report.getProjectGroupId(),
        report.getProjectArtifactId(),
        report.getProjectName()
    ));
    for (Discrepancy d : report.getDiscrepancies()) {
      getLog().info(String.format(
          "> %s:%s - (%s) %s != %s",
          d.getGroupId(),
          d.getArtifactId(),
          d.getImportedProjectScope(),
          d.getImportedProjectVersion(),
          d.getMainProjectVersion()
      ));
    }
  }
}
