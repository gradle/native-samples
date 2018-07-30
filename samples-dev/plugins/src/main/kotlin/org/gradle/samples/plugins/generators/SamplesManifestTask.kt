package org.gradle.samples.plugins.generators

import org.gradle.api.DefaultTask
import org.gradle.api.model.ObjectFactory
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.nio.file.Paths
import javax.inject.Inject

/**
 * Generates a manifest file listing the location of each generated sample.
 */
open class SamplesManifestTask @Inject constructor(objectFactory: ObjectFactory) : DefaultTask() {
    @OutputFile
    val manifest = newOutputFile()

    @Input
    val sampleDirs = objectFactory.setProperty(String::class.java)

    @Input
    val repoDirs = objectFactory.setProperty(String::class.java)

    @TaskAction
    fun generate() {
        manifest.get().asFile.printWriter().use { writer ->
            for (path in sampleDirs.get()) {
                val relPath = project.projectDir.toPath().relativize(Paths.get(path)).toString()
                writer.println("sample=$relPath")
            }
            for (path in repoDirs.get()) {
                val relPath = project.projectDir.toPath().relativize(Paths.get(path)).toString()
                writer.println("repo=$relPath")
            }
        }
    }
}