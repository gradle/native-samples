package org.gradle.samples.plugins.generators

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider
import org.gradle.samples.plugins.SampleGeneratorTask


class GeneratorPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val generatorTasks = project.tasks.withType(SampleGeneratorTask::class.java)
        val repoTasks = project.tasks.withType(GitRepoTask::class.java)

        // Add project extension
        val extension = project.extensions.create("samples", SamplesExtension::class.java, project)

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

        // Apply conventions to the generator tasks
        generatorTasks.configureEach { task ->
            task.templatesDir.set(project.file("src/templates"))
        }

        // Add a lifecycle task to generate the source files for the samples
        val generateSource = project.tasks.register("generateSource") { task ->
            task.dependsOn(generatorTasks)
            task.dependsOn(manifestTask)
            task.group = "source generation"
            task.description = "generate the source files for all samples"
        }

        extension.externalRepos.all {
            addTasksForRepo(it, generateSource, project)
        }


        // Add a lifecycle task to generate the repositories
        project.tasks.register("generateRepos") { task ->
            task.dependsOn(repoTasks)
            task.group = "source generation"
            task.description = "generate the Git repositories for all samples"
        }
    }

    private fun addTasksForRepo(repo: ExternalRepo, generateSource: TaskProvider<Task>, project: Project) {
        val syncTask = project.tasks.register("sync${repo.name.capitalize()}", SyncExternalRepoTask::class.java) { task ->
            task.repoUrl.set(repo.repoUrl)
            task.checkoutDirectory.set(project.file("repos/${repo.name}"))
        }
        val groovyTaskType = javaClass.classLoader.loadClass("org.gradle.sample.plugins.generators.SourceCopyTask").asSubclass(SampleGeneratorTask::class.java)
        val setupTask = project.tasks.register("copy${repo.name.capitalize()}", groovyTaskType) { task ->
            task.dependsOn(syncTask)
            task.sampleDir.set(syncTask.get().checkoutDirectory)
            task.doFirst {
                repo.sourceActions.forEach {
                    it.execute(task)
                }
            }
        }
        val updateTask = project.tasks.register("update${repo.name.capitalize()}", UpdateRepoTask::class.java) { task ->
            task.dependsOn(setupTask)
            task.sampleDir.set(syncTask.get().checkoutDirectory)
            repo.repoActions.forEach {
                task.change(it)
            }
        }
        generateSource.configure { task ->
            task.dependsOn(updateTask)
        }
    }
}