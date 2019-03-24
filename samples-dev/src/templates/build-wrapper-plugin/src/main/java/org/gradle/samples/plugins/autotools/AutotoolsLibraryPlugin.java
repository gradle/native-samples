package org.gradle.samples.plugins.autotools;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.samples.tasks.ConfigureTask;
import org.gradle.samples.tasks.Make;

public class AutotoolsLibraryPlugin implements Plugin<Project> {
    @Override
    public void apply(final Project project) {
        project.getPluginManager().apply("lifecycle-base");
        project.getPluginManager().apply("org.gradle.samples.wrapped-native-library");

        final AutotoolsExtension extension = project.getExtensions().create("autotools", AutotoolsExtension.class, project.getObjects());

        TaskProvider<ConfigureTask> configureDebug = project.getTasks().register("configureDebug", ConfigureTask.class, task -> {
            task.getSourceDirectory().set(extension.getSourceDirectory());
            task.getPrefixDirectory().set(project.getLayout().getBuildDirectory().dir("debug"));
            task.getMakeDirectory().set(project.getLayout().getBuildDirectory().dir("make-debug"));

            task.getArguments().addAll(extension.getConfigureArguments());
            task.getArguments().add("--enable-shared=no");
            task.getArguments().add("--enable-debug");
        });

        TaskProvider<Make> assembleDebug = project.getTasks().register("assembleDebug", Make.class, task -> {
            task.generatedBy(configureDebug);
            task.binary(extension.getBinary());
            task.getArguments().addAll(extension.getMakeArguments());
            task.getArguments().add("install");
        });

        TaskProvider<ConfigureTask> configureRelease = project.getTasks().register("configureRelease", ConfigureTask.class, task -> {
            task.getSourceDirectory().set(extension.getSourceDirectory());
            task.getPrefixDirectory().set(project.getLayout().getBuildDirectory().dir("release"));
            task.getMakeDirectory().set(project.getLayout().getBuildDirectory().dir("make-release"));

            task.getArguments().addAll(extension.getConfigureArguments());
            task.getArguments().add("--enable-shared=no");
            task.getArguments().add("--enable-debug");
            task.getArguments().add("--enable-optimizations");
        });

        TaskProvider<Make> assembleRelease = project.getTasks().register("assembleRelease", Make.class, task -> {
            task.generatedBy(configureRelease);
            task.binary(extension.getBinary());

            task.getArguments().addAll(extension.getMakeArguments());
            task.getArguments().add("install");
        });

        project.getConfigurations().getByName("headers").getOutgoing().artifact(extension.getIncludeDirectory());
        project.getConfigurations().getByName("linkDebug").getOutgoing().artifact(assembleDebug.flatMap(it -> it.getBinary()));
        project.getConfigurations().getByName("linkRelease").getOutgoing().artifact(assembleRelease.flatMap(it -> it.getBinary()));
    }
}
