package org.gradle.samples.plugins.autotools

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.samples.tasks.ConfigureTask
import org.gradle.samples.tasks.Make

class AutotoolsLibraryPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        project.pluginManager.apply("lifecycle-base")
        project.pluginManager.apply("org.gradle.samples.wrapped-native-library")

        def extension = project.extensions.create("autotools", AutotoolsExtension.class, project.objects)

        def configureDebug = project.tasks.create('configureDebug', ConfigureTask) {
            sourceDirectory.set extension.sourceDirectory
            prefixDirectory.set project.layout.buildDirectory.dir("debug")
            makeDirectory.set project.layout.buildDirectory.dir("make-debug")

            arguments.addAll(extension.configureArguments)
            arguments.add("--enable-shared=no")
            arguments.add("--enable-debug")
        }

        def assembleDebug = project.tasks.create('assembleDebug', Make) {
            generatedBy configureDebug
            binary extension.binary

            arguments.addAll(extension.makeArguments)
            arguments.add("install")
        }

        def configureRelease = project.tasks.create('configureRelease', ConfigureTask) {
            sourceDirectory.set extension.sourceDirectory
            prefixDirectory.set project.layout.buildDirectory.dir("release")
            makeDirectory.set project.layout.buildDirectory.dir("make-release")

            arguments.addAll(extension.configureArguments)
            arguments.add("--enable-shared=no")
            arguments.add("--enable-debug")
            arguments.add("--enable-optimizations")
        }

        def assembleRelease = project.tasks.create('assembleRelease', Make) {
            generatedBy configureRelease
            binary extension.binary

            arguments.addAll(extension.makeArguments)
            arguments.add("install")
        }

        project.configurations.headers.outgoing.artifact extension.includeDirectory
        project.configurations.linkDebug.outgoing.artifact assembleDebug.binary
        project.configurations.linkRelease.outgoing.artifact assembleRelease.binary
    }
}