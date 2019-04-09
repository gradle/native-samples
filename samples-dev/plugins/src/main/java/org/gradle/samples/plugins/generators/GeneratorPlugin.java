package org.gradle.samples.plugins.generators;

import com.google.common.base.CaseFormat;
import org.apache.commons.lang3.StringUtils;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.tasks.TaskCollection;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.samples.plugins.SampleGeneratorTask;

import java.util.stream.Collectors;

public class GeneratorPlugin implements Plugin<Project> {
    public void apply(Project project) {
        TaskCollection<SampleGeneratorTask> generatorTasks = project.getTasks().withType(SampleGeneratorTask.class);
        TaskCollection<GitRepoTask> repoTasks = project.getTasks().withType(GitRepoTask.class);

        // Add project extension
        SamplesExtension extension = project.getExtensions().create("samples", SamplesExtension.class, project);

        // Add a task to generate the list of samples
        TaskProvider<SamplesManifestTask> manifestTask = project.getTasks().register("samplesManifest", SamplesManifestTask.class, task -> {
            task.getManifest().set(project.file("samples-list.txt"));
            task.getSampleDirs().set(project.provider(() -> {
                return generatorTasks.stream().map(generator -> {
                    return generator.getSampleDir().get().getAsFile().getAbsolutePath();
                }).collect(Collectors.toList());
            }));
            task.getRepoDirs().set(project.provider(() -> {
                return repoTasks.stream().map(generator -> {
                    return generator.getSampleDir().get().getAsFile().getAbsolutePath();
                }).collect(Collectors.toList());
            }));
        });

        // Add a task to clean the samples
        project.getTasks().register("cleanSamples", CleanSamplesTask.class, task -> {
            // Need the location without the task dependency as we want to clean whatever was generated last time, not whatever will be generated next time
            task.getManifest().set(project.provider(() -> {
                return manifestTask.get().getManifest().get();
            }));
        });

        // Apply conventions to the generator tasks
        generatorTasks.configureEach( task -> {
            task.getTemplatesDir().set(project.file("src/templates"));
        });

        // Add a lifecycle task to generate the source files for the samples
        TaskProvider<Task> generateSource = project.getTasks().register("generateSource", task -> {
            task.dependsOn(generatorTasks);
            task.dependsOn(manifestTask);
            task.setGroup("source generation");
            task.setDescription("generate the source files for all samples");
        });

        extension.getExternalRepos().all(it -> {
            addTasksForRepo(it, generateSource, project);
        });

        extension.getSamples().all(it -> {
            addTasksForSample(it, project);
        });


        // Add a lifecycle task to generate the repositories
        project.getTasks().register("generateRepos", task -> {
            task.dependsOn(repoTasks);
            task.setGroup("source generation");
            task.setDescription("generate the Git repositories for all samples");
        });
    }

    private void addTasksForRepo(ExternalRepo repo, TaskProvider<Task> generateSource, Project project) {
        TaskProvider<SyncExternalRepoTask> syncTask = project.getTasks().register("sync" + StringUtils.capitalize(repo.getName()), SyncExternalRepoTask.class, task -> {
            task.getRepoUrl().set(repo.getRepoUrl());
            task.getCheckoutDirectory().set(project.file("repos/" + repo.getName()));
        });
        TaskProvider<SourceCopyTask> setupTask = project.getTasks().register("copy" + StringUtils.capitalize(repo.getName()), SourceCopyTask.class, task -> {
            task.dependsOn(syncTask);
            task.getSampleDir().set(syncTask.get().getCheckoutDirectory());
            task.doFirst(task1 -> {
                repo.getSourceActions().forEach(it -> {
                    it.execute(task);
                });
            });
        });
        TaskProvider<UpdateRepoTask> updateTask = project.getTasks().register("update" + StringUtils.capitalize(repo.getName()), UpdateRepoTask.class, task -> {
            task.dependsOn(setupTask);
            task.getSampleDir().set(syncTask.get().getCheckoutDirectory());
            repo.getRepoActions().forEach(it -> {
                task.change(it);
            });
        });
        generateSource.configure(task -> {
            task.dependsOn(updateTask);
        });
    }

    private void addTasksForSample(Sample sample, Project project) {
        String sampleNameCamelCase = CaseFormat.LOWER_HYPHEN.to(CaseFormat.LOWER_CAMEL, sample.getName());
        TaskProvider<SourceCopyTask> sourceCopyTask = project.getTasks().register(sampleNameCamelCase, SourceCopyTask.class, task -> {
            task.getSampleDir().set(sample.getSampleDir());
            sample.getSourceActions().forEach( it -> it.execute(task));
        });
    }
}
