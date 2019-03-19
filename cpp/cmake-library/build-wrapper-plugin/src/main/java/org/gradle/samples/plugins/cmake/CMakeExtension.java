package org.gradle.samples.plugins.cmake;

import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.ProjectLayout;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;

import javax.inject.Inject;

public class CMakeExtension {
    private final Property<String> binary;
    private final DirectoryProperty includeDirectory;
    private final DirectoryProperty projectDirectory;

    @Inject
    public CMakeExtension(ProjectLayout projectLayout, ObjectFactory objectFactory) {
        binary = objectFactory.property(String.class);
        includeDirectory = objectFactory.directoryProperty();
        projectDirectory = objectFactory.directoryProperty();
        projectDirectory.set(projectLayout.getProjectDirectory());
        includeDirectory.set(projectDirectory.dir("include"));
    }

    public final Property<String> getBinary() {
        return binary;
    }

    public final DirectoryProperty getIncludeDirectory() {
        return includeDirectory;
    }

    public final DirectoryProperty getProjectDirectory() {
        return projectDirectory;
    }
}
