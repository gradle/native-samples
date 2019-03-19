package org.gradle.samples.plugins;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class WrappedNativeBasePlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        // Apply the base plugin, to define 'clean' task and other things
        project.getPluginManager().apply("lifecycle-base");
    }

}
