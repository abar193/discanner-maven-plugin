package me.mrabar.discrepancy.utils;

import org.apache.maven.model.Dependency;
import org.apache.maven.project.MavenProject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DependencyUtils {
  private static String dependencyKey(Dependency d) {
    return String.format("%s:%s", d.getGroupId(), d.getArtifactId());
  }

  private static String dependencyVersion(Dependency d) {
    return d.getVersion();
  }

  public static Map<String, String> dependencyVersionMap(List<Dependency> dependencies) {
    Map<String, String> map = new HashMap<>();
    dependencies.forEach(d -> map.put(dependencyKey(d), dependencyVersion(d)));
    return map;
  }

  public static boolean isIncluded(MavenProject project, Map<String, String> dependencies) {
    String projectKey = String.format("%s:%s", project.getGroupId(), project.getArtifactId());
    return dependencies.containsKey(projectKey);
  }
}
