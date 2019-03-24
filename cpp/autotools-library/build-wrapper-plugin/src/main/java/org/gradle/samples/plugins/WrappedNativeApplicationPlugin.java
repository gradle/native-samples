package org.gradle.samples.plugins;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.attributes.Usage;
import org.gradle.language.cpp.CppBinary;

public class WrappedNativeApplicationPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        project.getPluginManager().apply("org.gradle.samples.wrapped-native-base");

        // Add configurations for incoming dependencies
        final Usage cppApiUsage = project.getObjects().named(Usage.class, Usage.C_PLUS_PLUS_API);
        final Usage linkUsage = project.getObjects().named(Usage.class, Usage.NATIVE_LINK);

        Configuration implementation = project.getConfigurations().create("implementation", it -> {
            it.setCanBeConsumed(false);
            it.setCanBeResolved(false);
        });

        // incoming compile time headers
        project.getConfigurations().create("cppCompile", it -> {
            it.setCanBeConsumed(false);
            it.getAttributes().attribute(Usage.USAGE_ATTRIBUTE, cppApiUsage);
            it.extendsFrom(implementation);
        });

        // incoming link files
        project.getConfigurations().create("linkDebug", it -> {
            it.setCanBeConsumed(false);
            it.getAttributes().attribute(Usage.USAGE_ATTRIBUTE, linkUsage);
            it.getAttributes().attribute(CppBinary.DEBUGGABLE_ATTRIBUTE, true);
            it.getAttributes().attribute(CppBinary.OPTIMIZED_ATTRIBUTE, false);
            it.extendsFrom(implementation);
        });
        project.getConfigurations().create("linkRelease", it -> {
            it.setCanBeConsumed(false);
            it.getAttributes().attribute(Usage.USAGE_ATTRIBUTE, linkUsage);
            it.getAttributes().attribute(CppBinary.DEBUGGABLE_ATTRIBUTE, true);
            it.getAttributes().attribute(CppBinary.OPTIMIZED_ATTRIBUTE, true);
            it.extendsFrom(implementation);
        });
    }
}
