package org.gradle.samples.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.ListProperty

class ConfigureTask extends DefaultTask {
    @InputDirectory
    DirectoryProperty sourceDirectory

    @OutputDirectory
    DirectoryProperty makeDirectory

    @Internal
    DirectoryProperty prefixDirectory

    @Input
    ListProperty<String> arguments

    public ConfigureTask() {
        sourceDirectory = newInputDirectory()
        makeDirectory = newOutputDirectory()
        prefixDirectory = newInputDirectory()
        arguments = getProject().getObjects().listProperty(String.class)
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