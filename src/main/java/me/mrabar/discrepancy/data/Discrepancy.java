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

public class Discrepancy {
  private final String groupId;
  private final String artifactId;
  private final String mainProjectVersion;
  private final String importedProjectVersion;
  private final String importedProjectScope;

  public Discrepancy(
      String groupId,
      String artifactId,
      String mainProjectVersion,
      String importedProjectVersion,
      String importedProjectScope
  ) {
    this.groupId = groupId;
    this.artifactId = artifactId;
    this.mainProjectVersion = mainProjectVersion;
    this.importedProjectVersion = importedProjectVersion;
    this.importedProjectScope = importedProjectScope;
  }

  public String getGroupId() {
    return groupId;
  }

  public String getArtifactId() {
    return artifactId;
  }

  public String getMainProjectVersion() {
    return mainProjectVersion;
  }

  public String getImportedProjectVersion() {
    return importedProjectVersion;
  }

  public String getImportedProjectScope() {
    return importedProjectScope;
  }


}
