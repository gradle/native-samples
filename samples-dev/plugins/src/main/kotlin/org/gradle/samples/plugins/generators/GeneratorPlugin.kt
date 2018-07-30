package org.gradle.samples.plugins.generators

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.samples.plugins.GeneratorTask


class GeneratorPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val generatorTasks = project.tasks.withType(GeneratorTask::class.java)

        // Add a task to generate the list of samples
        val manifestTask = project.tasks.register("samplesManifest", SamplesManifestTask::class.java) { task ->
            task.manifest.set(project.file("samples-list.txt"))
            task.sampleDirs.set(project.provider {
                generatorTasks.map { generator ->
                    project.projectDir.toPath().relativize(generator.sampleDir.get().asFile.toPath()).toString()
                }
            })
        }

        // Add a task to clean the samples
        project.tasks.register("cleanSamples", CleanSamplesTask::class.java) { task ->
            // Need the location without the task dependency as we want to clean whatever was generated last time, not whatever will be generated next time
            task.manifest.set(project.provider {
                manifestTask.get().manifest.get()
            })
        }

        generatorTasks.configureEach { task ->
            task.templatesDir.set(project.file("src/templates"))
            task.group = "source generation"
        }

        // Add a lifecycle task to generate everything
        project.tasks.register("generateSource") { task ->
            task.dependsOn(generatorTasks)
            task.dependsOn(manifestTask)
            task.group = "source generation"
            task.description = "generate the source files for all samples"
        }
    }
}