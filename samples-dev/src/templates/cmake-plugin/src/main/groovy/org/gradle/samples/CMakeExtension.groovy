package org.gradle.samples

import org.gradle.api.model.ObjectFactory

import org.gradle.api.provider.Property

import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.ProjectLayout
import org.gradle.api.file.RegularFileProperty

import javax.inject.Inject

class CMakeExtension {
    final Property<String> binary
    final DirectoryProperty includeDirectory
    final DirectoryProperty projectDirectory

    @Inject
    CMakeExtension(ProjectLayout projectLayout, ObjectFactory objectFactory) {
        binary = objectFactory.property(String)
        includeDirectory = projectLayout.directoryProperty()
        projectDirectory = projectLayout.directoryProperty()
        projectDirectory.set(projectLayout.projectDirectory)
        includeDirectory.set(projectDirectory.dir("include"))
    }
}