package org.gradle.samples.plugins.generators

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.samples.plugins.RepoGeneratorTask
import org.gradle.samples.plugins.SampleGeneratorTask


class GeneratorPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val generatorTasks = project.tasks.withType(SampleGeneratorTask::class.java)
        val repoTasks = project.tasks.withType(RepoGeneratorTask::class.java)

        // Add a task to generate the list of samples
        val manifestTask = project.tasks.register("samplesManifest", SamplesManifestTask::class.java) { task ->
            task.manifest.set(project.file("samples-list.txt"))
            task.sampleDirs.set(project.provider {
                generatorTasks.map { generator ->
                    generator.sampleDir.get().asFile.absolutePath
                }
            })
            task.repoDirs.set(project.provider {
                repoTasks.map { generator ->
                    generator.sampleDir.get().asFile.absolutePath
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
        }

        // Add a lifecycle task to generate everything
        project.tasks.register("generateSource") { task ->
            task.dependsOn(generatorTasks)
            task.dependsOn(manifestTask)
            task.group = "source generation"
            task.description = "generate the source files for all samples"
        }

        // Add a lifecycle task to generate the repositories
        project.tasks.register("generateRepos") { task ->
            task.dependsOn(repoTasks)
            task.group = "source generation"
            task.description = "generate the Git repositories for all samples"
        }
    }
}