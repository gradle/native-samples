package org.gradle.samples.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.*

import javax.inject.Inject

class ConfigureTask extends DefaultTask {
    @InputDirectory
    DirectoryProperty sourceDirectory

    @OutputDirectory
    DirectoryProperty makeDirectory

    @Internal
    DirectoryProperty prefixDirectory

    @Input
    ListProperty<String> arguments

    @Inject
    ConfigureTask(ObjectFactory objectFactory) {
        sourceDirectory = objectFactory.directoryProperty()
        makeDirectory = objectFactory.directoryProperty()
        prefixDirectory = objectFactory.directoryProperty()
        arguments = objectFactory.listProperty(String.class).empty()
    }

    @TaskAction
    void runConfigure() {
        project.exec {
            workingDir makeDirectory

            List<String> allArguments = [sourceDirectory.file('configure').get().asFile, "--prefix=${prefixDirectory.get().asFile}"]
            allArguments.addAll arguments.get()
            commandLine allArguments
        }
    }
}
