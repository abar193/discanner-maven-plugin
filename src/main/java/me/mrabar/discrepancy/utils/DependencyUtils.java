/*
 * Copyright 2022 Anton Bardishev
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package me.mrabar.discrepancy.utils;

import me.mrabar.discrepancy.data.Discrepancy;
import org.apache.maven.model.Dependency;
import org.apache.maven.project.MavenProject;

import java.util.*;


public class DependencyUtils {
  private static final Set<String> IGNORED_SCOPES = new HashSet<>(Arrays.asList("test", "system"));

  private static String dependencyKey(Dependency d) {
    return String.format("%s:%s", d.getGroupId(), d.getArtifactId());
  }

  private static String dependencyVersion(Dependency d) {
    return d.getVersion();
  }

  public static Map<String, Dependency> dependencyMap(List<Dependency> dependencies) {
    Map<String, Dependency> map = new HashMap<>();
    dependencies.forEach(d -> map.put(dependencyKey(d), d));
    return map;
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
}
