/*
 * Copyright 2022 Anton Bardishev
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and
 * to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of
 * the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 */

package me.mrabar.discrepancy.utils;

import me.mrabar.discrepancy.data.ChildrenStrategy;
import me.mrabar.discrepancy.data.Discrepancy;
import org.apache.maven.model.Dependency;
import org.apache.maven.project.MavenProject;

import java.util.*;
import java.util.stream.Collectors;


public class DependencyUtils {
  private static final Set<String> IGNORED_SCOPES = new HashSet<>(Arrays.asList("test", "system"));

  public static Map<String, Dependency> dependencyMap(MavenProject project, ChildrenStrategy strategy) {
    Map<String, Dependency> dependencies = mapDependencies(project);

    if (ChildrenStrategy.IGNORE.equals(strategy)) {
      return dependencies;
    }

    List<Map<String, Dependency>> childrenDeps = project.getCollectedProjects()
        .stream()
        .map(DependencyUtils::mapDependencies)
        .collect(Collectors.toList());

    return merge(dependencies, childrenDeps, ChildrenStrategy.VERIFY.equals(strategy));
  }

  private static Map<String, Dependency> mapDependencies(MavenProject project) {
    Map<String, Dependency> map = new HashMap<>();

    project.getDependencies().forEach(d -> map.put(dependencyKey(d), d));
    return map;
  }

  private static Map<String, Dependency> merge(
      Map<String, Dependency> main,
      List<Map<String, Dependency>> children,
      boolean verify
  ) {
    Map<String, Dependency> result = new HashMap<>(main);
    for (Map<String, Dependency> child : children) {
      if(verify) {
        verify(result, child);
      }
      result.putAll(child);
    }
    return result;
  }

  private static void verify(Map<String, Dependency> a, Map<String, Dependency> b) {
    // Could improve by first collecting all the dependencies, and failing only afterwards
    Set<String> keys = a.keySet();
    keys.retainAll(b.keySet());
    for(String k : keys) {
      boolean versionsDiffer = !a.get(k).getVersion().equals(b.get(k).getVersion());
      if(versionsDiffer) {
        throw new RuntimeException(String.format(
            "Dependency %s imported under different versions: %s != %s",
            k,
            a.get(k).getVersion(),
            b.get(k).getVersion()
        ));
      }
    }

  }

  public static boolean isIncluded(MavenProject project, Map<String, Dependency> dependencies) {
    String projectKey = String.format("%s:%s", project.getGroupId(), project.getArtifactId());
    return dependencies.containsKey(projectKey);
  }

  /**
   * Compares dependency from imported project and checks if it's version differs in main project.
   *
   * @return a new Discrepancy if dependency version differ, null otherwise
   */
  public static Discrepancy checkDiscrepancy(Dependency imported, Map<String, Dependency> dependencies) {
    if (IGNORED_SCOPES.contains(imported.getScope())) {
      return null;
    }

    String key = dependencyKey(imported);
    if (!dependencies.containsKey(key)) {
      return null; // assume it's ok for now
    }

    Dependency main = dependencies.get(key);
    if (main.getVersion().equals(imported.getVersion())) {
      return null;
    }

    return new Discrepancy(
        imported.getGroupId(),
        imported.getArtifactId(),
        main.getVersion(),
        imported.getVersion(),
        imported.getScope()
    );
  }

  private static String dependencyKey(Dependency d) {
    return String.format("%s:%s", d.getGroupId(), d.getArtifactId());
  }
}
