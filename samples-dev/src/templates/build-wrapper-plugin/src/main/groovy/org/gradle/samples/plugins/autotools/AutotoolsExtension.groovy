package org.gradle.samples.plugins.autotools

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
    AutotoolsExtension(ObjectFactory objectFactory) {
        binary = objectFactory.property(String.class)
        includeDirectory = objectFactory.directoryProperty()
        sourceDirectory = objectFactory.directoryProperty()
        configureArguments = objectFactory.listProperty(String).empty()
        makeArguments = objectFactory.listProperty(String).empty()
    }
}
