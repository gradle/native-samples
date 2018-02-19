package org.gradle.samples

import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.ProjectLayout
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property

import javax.inject.Inject

class AutotoolsExtension {
    final Property<String> binary
    final DirectoryProperty includeDirectory
    final DirectoryProperty sourceDirectory
    final ListProperty<String> configureArguments
    final ListProperty<String> makeArguments

    @Inject
    AutotoolsExtension(ProjectLayout projectLayout, ObjectFactory objectFactory) {
        binary = objectFactory.property(String.class)
        includeDirectory = projectLayout.directoryProperty()
        sourceDirectory = projectLayout.directoryProperty()
        configureArguments = objectFactory.listProperty(String)
        makeArguments = objectFactory.listProperty(String)
    }
}