package org.gradle.samples.plugins.cmake;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.samples.tasks.CMake;
import org.gradle.samples.tasks.Make;

/**
 * A sample plugin that wraps a CMake build with Gradle to take care of dependency management.
 */
public class CMakeApplicationPlugin implements Plugin<Project> {
    public void apply(final Project project) {
        project.getPluginManager().apply("org.gradle.samples.wrapped-native-application");

        /*
         * Create some tasks to drive the CMake build
         */
        TaskContainer tasks = project.getTasks();

        TaskProvider<CMake> cmakeDebug = tasks.register("cmakeDebug", CMake.class, task -> {
            task.setBuildType("Debug");
            task.getIncludeDirs().from(project.getConfigurations().getByName("cppCompile"));
            task.getLinkFiles().from(project.getConfigurations().getByName("linkDebug"));
            task.getVariantDirectory().set(project.getLayout().getBuildDirectory().dir("debug"));
            task.getProjectDirectory().set(project.getLayout().getProjectDirectory());
        });

        TaskProvider<CMake> cmakeRelease = tasks.register("cmakeRelease", CMake.class, task -> {
            task.setBuildType("RelWithDebInfo");
            task.getIncludeDirs().from(project.getConfigurations().getByName("cppCompile"));
            task.getLinkFiles().from(project.getConfigurations().getByName("linkRelease"));
            task.getVariantDirectory().set(project.getLayout().getBuildDirectory().dir("release"));
            task.getProjectDirectory().set(project.getLayout().getProjectDirectory());
        });

        TaskProvider<Make> assembleDebug = tasks.register("assembleDebug", Make.class, task -> {
            task.setGroup("Build");
            task.setDescription("Builds the debug binaries");
            task.generatedBy(cmakeDebug);
            task.binary(project.provider(() -> project.getName()));
        });

        TaskProvider<Make> assembleRelease = tasks.register("assembleRelease", Make.class, task -> {
            task.setGroup("Build");
            task.setDescription("Builds the release binaries");
            task.generatedBy(cmakeRelease);
            task.binary(project.provider(() -> project.getName()));
        });

        tasks.named("assemble", task -> task.dependsOn(assembleDebug));
    }
}
