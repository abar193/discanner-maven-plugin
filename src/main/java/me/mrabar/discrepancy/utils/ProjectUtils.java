package me.mrabar.discrepancy.utils;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.apache.maven.project.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProjectUtils {
  private static final Log log = new SystemStreamLog();

  public static List<MavenProject> scanProjects(
      ProjectBuilder builder,
      MavenSession session,
      String projectScanDir
  ) {
    List<MavenProject> projects = new ArrayList<>();
    File[] directories = new File(projectScanDir).listFiles(File::isDirectory);

    for (File dir : directories) {
      File pom = new File(dir, "pom.xml");
      if (pom.exists()) {
        buildProject(pom, session, builder).ifPresent(projects::add);
      }
    }

    return projects;
  }

  private static Optional<MavenProject> buildProject(File pom, MavenSession session, ProjectBuilder builder) {
    try {
      ProjectBuildingRequest prb = new DefaultProjectBuildingRequest(session.getProjectBuildingRequest());
      ProjectBuildingResult result = builder.build(pom, prb);
      return Optional.of(result.getProject());
    } catch (ProjectBuildingException e) {
      log.error(e);
      return Optional.empty();
    }
  }
}
