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

import org.apache.maven.plugin.logging.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ProjectUtils {
  public static List<File> scanProjects(Log log, String projectScanDir) {
    File scanDir = new File(projectScanDir);
    if(!scanDir.exists()) {
      log.error(projectScanDir + " not found!");
      throw new RuntimeException(projectScanDir + " not found!");
    }

    File[] directories = new File(projectScanDir).listFiles(File::isDirectory);
    if(directories == null || directories.length == 0) {
      log.error("No project folders found in " + scanDir.getAbsolutePath());
      throw new RuntimeException("No project folders found in " + scanDir.getAbsolutePath());
    }

    List<File> projects = new ArrayList<>();
    for (File dir : directories) {
      File pom = new File(dir, "pom.xml");
      if (pom.exists()) {
        projects.add(pom);
      }
    }

    return projects;
  }

}
