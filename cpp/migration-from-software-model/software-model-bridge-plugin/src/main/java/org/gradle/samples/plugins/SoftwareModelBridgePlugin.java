package org.gradle.samples.plugins;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.AppliedPlugin;
import org.gradle.samples.SoftwareModelComponentSelection;
import org.gradle.samples.internal.DefaultSoftwareModelComponentSelection;
import org.gradle.samples.internal.SoftwareModelBridgeRules;

public class SoftwareModelBridgePlugin implements Plugin<Project> {
    public void apply(Project project) {
        project.getPluginManager().withPlugin("cpp", new Action<AppliedPlugin>() {
            @Override
            public void execute(AppliedPlugin appliedPlugin) {
                project.getPluginManager().apply(SoftwareModelBridgeRules.class);
            }
        });

        project.getExtensions().create(SoftwareModelComponentSelection.class, "component", DefaultSoftwareModelComponentSelection.class, project);
    }
}