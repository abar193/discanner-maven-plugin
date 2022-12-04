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

import me.mrabar.discrepancy.data.Discrepancy;
import me.mrabar.discrepancy.data.DiscrepancyReport;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class CsvWriter {
  public static void reportCsv(MavenProject project, String fileName, List<DiscrepancyReport> reports) {
    File outDir = new File(fileName);
    if (!outDir.exists() || !outDir.isDirectory()) {
      throw new RuntimeException("Directory " + outDir.getAbsolutePath() + " does not exist!");
    }

    File out = new File(outDir, project.getArtifactId() + ".csv");

    try (PrintWriter writer = new PrintWriter(new FileWriter(out));) {
      writer.println("project name; project group; project artifact; discrepancy group; discrepancy artifact; "
                         + "discrepancy scope; discrepancy version; expected version");
      for (DiscrepancyReport r : reports) {
        for (Discrepancy d : r.getDiscrepancies()) {
          writer.println(String.format(
              "%s; %s; %s; %s; %s; %s; %s; %s",
              r.getProjectName(),
              r.getProjectGroupId(),
              r.getProjectArtifactId(),
              d.getGroupId(),
              d.getArtifactId(),
              d.getImportedProjectScope(),
              d.getImportedProjectVersion(),
              d.getMainProjectVersion()
          ));
        }
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
