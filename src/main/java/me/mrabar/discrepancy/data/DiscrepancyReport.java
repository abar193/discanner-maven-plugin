/*
 * Copyright 2022 Anton Bardishev
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package me.mrabar.discrepancy.data;

import java.util.List;
import java.util.StringJoiner;

public class DiscrepancyReport {
  private final String projectGroupId;
  private final String projectArtifactId;
  private final String projectName;
  private final List<Discrepancy> discrepancies;

  public DiscrepancyReport(
      String projectGroupId,
      String projectArtifactId,
      String projectName,
      List<Discrepancy> discrepancies
  ) {
    this.projectGroupId = projectGroupId;
    this.projectArtifactId = projectArtifactId;
    this.projectName = projectName;
    this.discrepancies = discrepancies;
  }

  public String getProjectGroupId() {
    return projectGroupId;
  }

  public String getProjectArtifactId() {
    return projectArtifactId;
  }

  public String getProjectName() {
    return projectName;
  }

  public List<Discrepancy> getDiscrepancies() {
    return discrepancies;
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", DiscrepancyReport.class.getSimpleName() + "[", "]")
        .add("projectGroupId='" + projectGroupId + "'")
        .add("projectArtifactId='" + projectArtifactId + "'")
        .add("projectName='" + projectName + "'")
        .add("discrepancies=" + discrepancies)
        .toString();
  }
}
