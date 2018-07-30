package org.gradle.samples.plugins.generators

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction
import java.io.File
import javax.inject.Inject

/**
 * Cleans each of the generated samples listed in the samples manifest.
 */
open class CleanSamplesTask @Inject constructor() : DefaultTask() {
    private val names = setOf("repo", "build", ".gradle", ".build", "Package.resolved")
    private val extensions = setOf("xcworkspace", "xcodeproj", "vs", "sln", "vcxproj", "vcxproj.filters", "vcxproj.user")

    @InputFile
    val manifest = newInputFile()

    @TaskAction
    fun clean() {
        manifest.get().asFile.readLines().forEach { item ->
            if (item.startsWith("sample=")) {
                val path = item.substring(7)
                val dir = project.file(path)
                if (dir.isDirectory) {
                    cleanDir(dir)
                }
            } else if (item.startsWith("repo=")) {
                val path = item.substring(5)
                val dir = project.file(path)
                if (File(dir, ".git").isDirectory) {
                    delete(dir)
                }
            }
        }
    }

    private fun cleanDir(dir: File) {
        dir.listFiles().forEach { file ->
            if (names.contains(file.name)) {
                delete(file)
            } else if (extensions.contains(file.name.substringAfterLast("."))) {
                delete(file)
            } else if (file.isDirectory) {
                cleanDir(file)
            }
        }
    }

    private fun delete(file: File) {
        println("Cleaning $file")
        file.deleteRecursively()
    }
}