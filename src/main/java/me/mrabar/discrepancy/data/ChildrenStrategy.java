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

package me.mrabar.discrepancy.data;

/**
 * How to handle children projects, included in the main one (for which we run plugin)
 */
public enum ChildrenStrategy {
  /**
   * Ignore children projects, only check dependencies of a main project,
   */
  IGNORE,
  /**
   * Default option. Include all the dependencies from the children projects, do not verify if they match.
   * <p/>
   * If a project A has children projects B and C, and B imports D version 1.0.0 and C imports D
   * version 1.0.1, then it is not determined if D will be inspected as version 1.0.0 or 1.0.1.
   */
  INCLUDE,
  /**
   * Same as with INCLUDE, but the task will fail if a root project has conflicting versions of libraries
   * among its dependencies.
   */
  VERIFY;

  public static ChildrenStrategy of(String name) {
    try {
      return valueOf(name.toUpperCase());
    } catch (IllegalArgumentException e) {
      System.err.println("Invalid ChildrenStrategy " + name + " provided, going with 'include'");
      return INCLUDE;
    }
  }
}
