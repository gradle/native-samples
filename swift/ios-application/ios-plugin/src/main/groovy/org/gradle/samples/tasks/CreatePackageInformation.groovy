package org.gradle.samples.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

@CacheableTask
class CreatePackageInformation extends DefaultTask {
    @OutputFile
    final RegularFileProperty outputFile = newOutputFile()

    @TaskAction
    private void doCreate() {
        // Note: Oversimplification of how this file is created
        outputFile.get().asFile.text = "APPL????"
    }
}
