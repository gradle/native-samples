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
    DirectoryProperty sourceDir

    @OutputDirectory
    DirectoryProperty makeDir

    @Internal
    DirectoryProperty prefixDir

    @Input
    ListProperty<String> arguments

    public ConfigureTask() {
        sourceDir = newInputDirectory()
        makeDir = newOutputDirectory()
        prefixDir = newInputDirectory()
        arguments = getProject().getObjects().listProperty(String.class)
    }

    @TaskAction
    void runConfigure() {
        project.exec {
            workingDir makeDir

            List<String> allArguments = [sourceDir.file('configure').get().asFile, "--prefix=${prefixDir.get().asFile}"]
            allArguments.addAll arguments.get()
            commandLine allArguments
        }
    }
}