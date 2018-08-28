package org.gradle.samples;

import org.gradle.api.Project;
import org.gradle.api.attributes.Attribute;

public interface SoftwareModelComponentSelection {
    Attribute<String> COMPONENT_NAME_ATTRIBUTE = Attribute.of("software-model-component-name", String.class);

    Project from(String projectPath, String componentName);
}
