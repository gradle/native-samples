package org.gradle.samples.plugins.cmake;

import groovy.lang.Closure;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.samples.tasks.CMake;
import org.gradle.samples.tasks.Make;

/**
 * A sample plugin that wraps a CMake build with Gradle to take care of dependency management.
 */
public class CMakeLibraryPlugin implements Plugin<Project> {
    public void apply(final Project project) {
        project.getPluginManager().apply("org.gradle.samples.wrapped-native-library");

        // Add a CMake extension to the Gradle model
        final CMakeExtension extension = project.getExtensions().create("cmake", CMakeExtension.class, project.getLayout(), project.getObjects());

        /*
         * Create some tasks to drive the CMake build
         */
        TaskContainer tasks = project.getTasks();

        final TaskProvider<CMake> cmakeDebug = tasks.register("cmakeDebug", CMake.class, task -> {
                task.setBuildType("Debug");
                task.getIncludeDirs().from(project.getConfigurations().getByName("cppCompile"));
                task.getLinkFiles().from(project.getConfigurations().getByName("cppLinkDebug"));
                task.getVariantDirectory().set(project.getLayout().getBuildDirectory().dir("debug"));
                task.getProjectDirectory().set(extension.getProjectDirectory());
        });

        final TaskProvider<CMake> cmakeRelease = tasks.register("cmakeRelease", CMake.class, task -> {
            task.setBuildType("RelWithDebInfo");
            task.getIncludeDirs().from(project.getConfigurations().getByName("cppCompile"));
            task.getLinkFiles().from(project.getConfigurations().getByName("cppLinkRelease"));
            task.getVariantDirectory().set(project.getLayout().getBuildDirectory().dir("release"));
            task.getProjectDirectory().set(extension.getProjectDirectory());
        });

        final TaskProvider<Make> assembleDebug = tasks.register("assembleDebug", Make.class, task -> {
            task.setGroup("Build");
            task.setDescription("Builds the debug binaries");
            task.generatedBy(cmakeDebug);
            task.binary(extension.getBinary());
        });

        TaskProvider<Make> assembleRelease = tasks.register("assembleRelease", Make.class, task -> {
            task.setGroup("Build");
            task.setDescription("Builds the release binaries");
            task.generatedBy(cmakeRelease);
            task.binary(extension.getBinary());
        });

        tasks.named("assemble", task -> task.dependsOn(assembleDebug));

        /*
         * Configure the artifacts which should be exposed by this build
         * to other Gradle projects. (Note that this build does not currently
         * expose any runtime (shared library) artifacts)
         */
        ConfigurationContainer configurations = project.getConfigurations();
        configurations.getByName("headers").getOutgoing().artifact(extension.getIncludeDirectory());
        configurations.getByName("linkDebug").getOutgoing().artifact(assembleDebug.flatMap(it -> it.getBinary()));
        configurations.getByName("linkRelease").getOutgoing().artifact(assembleRelease.flatMap(it -> it.getBinary()));
    }
}
