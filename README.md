Discrepancy Scanner Maven Plugin
---

A plugin for large (legacy and/or enterprise) projects, with lots of internal projects and 
conflicting dependencies.

Compares dependencies from the project, where this plugin is involved, with 
dependencies from other projects on your local machine (if those projects are imported
in your project).

If you don't understand why it's needed or what is this for then you are a happy person 
and I envy you.

Usage
---

1. Checkout project locally, run `mvn install`. 
2. If it builds, add following block to your code: 

    ```
    <build>
        <plugins>
            ...
            <plugin>
                <groupId>me.mrabar.discrepancy</groupId>
                <artifactId>discanner-maven-plugin</artifactId>
                <version>1.0-SNAPSHOT</version>
                <configuration>
                    <projectScanDir>/path/to/your/projects/</projectScanDir>
                    <!-- For parallel builds and faster results -->
                    <!-- <threads>4</threads> --> 
                    <!-- Uncomment if you want a csv report -->
                    <!-- <csvReportLocation>out.csv</csvReportLocation> -->
                </configuration>
            </plugin>
        </plugins>
    </build>
    ```
   
3. Run `mvn discanner:scan` (or `me.mrabar.discrepancy:discanner-maven-plugin:scan`)
4. Report all the errors, bugs and crashes that you get, because it's extremely fresh project and it hasn't been 
   properly tested yet.

