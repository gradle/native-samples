package org.gradle.samples.internal;

import org.gradle.api.Action;
import org.gradle.api.ActionConfiguration;
import org.gradle.api.Project;
import org.gradle.api.attributes.Attribute;
import org.gradle.samples.SoftwareModelComponentSelection;

public class DefaultSoftwareModelComponentSelection implements SoftwareModelComponentSelection {
    private final Project project;
    private boolean isAlreadyCalled = false;

    public DefaultSoftwareModelComponentSelection(Project project) {
        this.project = project;
    }

    public Project from(String projectPath, String componentName) {
        if (isAlreadyCalled) {
            throw new IllegalStateException("Can't be used for more than one component");
        }

        try {
            project.getDependencies().getAttributesSchema().attribute(COMPONENT_NAME_ATTRIBUTE).getDisambiguationRules().add(SoftwareModelComponentSelectionDisambiguationRule.class, new Action<ActionConfiguration>() {
                @Override
                public void execute(ActionConfiguration configuration) {
                    configuration.params(componentName);
                }
            });

            return project.project(projectPath);
        } finally {
            isAlreadyCalled = true;
        }
    }
}