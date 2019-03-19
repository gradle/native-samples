package org.gradle.samples.plugins.autotools;

import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;

import javax.inject.Inject;

public class AutotoolsExtension {
    private final Property<String> binary;
    private final DirectoryProperty includeDirectory;
    private final DirectoryProperty sourceDirectory;
    private final ListProperty<String> configureArguments;
    private final ListProperty<String> makeArguments;

    @Inject
    public AutotoolsExtension(ObjectFactory objectFactory) {
        binary = objectFactory.property(String.class);
        includeDirectory = objectFactory.directoryProperty();
        sourceDirectory = objectFactory.directoryProperty();
        configureArguments = objectFactory.listProperty(String.class).empty();
        makeArguments = objectFactory.listProperty(String.class).empty();
    }

    public Property<String> getBinary() {
        return binary;
    }

    public DirectoryProperty getIncludeDirectory() {
        return includeDirectory;
    }

    public DirectoryProperty getSourceDirectory() {
        return sourceDirectory;
    }

    public ListProperty<String> getConfigureArguments() {
        return configureArguments;
    }

    public ListProperty<String> getMakeArguments() {
        return makeArguments;
    }
}
