package me.mrabar.discrepancy;

import me.mrabar.discrepancy.utils.DependencyUtils;
import me.mrabar.discrepancy.utils.ProjectUtils;
import org.apache.maven.Maven;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.project.ProjectBuildingRequest;

import java.util.List;
import java.util.Map;

@Mojo(
    name = "scan",
    requiresDependencyResolution = ResolutionScope.COMPILE
)
public class DiscrepancyMojo extends AbstractMojo {
  @Parameter(defaultValue = "${session}", required = true, readonly = true)
  private MavenSession session;
  @Component
  private ProjectBuilder mavenProjectBuilder;

//  @Component
//  private ProjectBuilderConfiguration projectBuilderConfiguration;
  @Parameter(property = "projectScanDir")
  String projectScanDir;

  @SuppressWarnings("unchecked")
  public void execute() throws MojoExecutionException, MojoFailureException {

    MavenProject mainProject = (MavenProject) getPluginContext().get("project");
    PluginDescriptor pluginDescriptor = (PluginDescriptor) getPluginContext().get("pluginDescriptor");

    Map<String, String> map = DependencyUtils.dependencyVersionMap(mainProject.getDependencies());
    for (String s : map.keySet()) {
      getLog().info(s + " " + map.get(s));
    }

    List<MavenProject> detectedProjects = ProjectUtils.scanProjects(
        mavenProjectBuilder,
        session,
        projectScanDir
    );

    for(MavenProject d : detectedProjects) {
      if(DependencyUtils.isIncluded(d, map)) {
        getLog().info("Imported project: " + d.getArtifactId());
        Map<String, String> deps = DependencyUtils.dependencyVersionMap(d.getDependencies());
        for (String s : deps.keySet()) {
          getLog().info("> " + s + " " + deps.get(s));
          if(map.containsKey(s)) {
            getLog().info("= " + map.get(s) + " <-> " + deps.get(s));
          }
        }
      }
    }
  }
}
