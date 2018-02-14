package org.gradle.samples

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.attributes.Usage
import org.gradle.language.cpp.CppBinary

/**
 * A sample plugin that wraps a CMake build with Gradle to take care of dependency management.
 */
class CMakeApplicationPlugin implements Plugin<Project> {
    void apply(Project project) {
        // Apply the base plugin, to define 'clean' task and other things
        project.pluginManager.apply("lifecycle-base")

        // Add configurations for incoming dependencies
        def cppApiUsage = project.objects.named(Usage.class, Usage.C_PLUS_PLUS_API)
        def linkUsage = project.objects.named(Usage.class, Usage.NATIVE_LINK)

        project.configurations {
            implementation {
                canBeConsumed = false
                canBeResolved = false
            }

            // incoming compile time headers
            cppCompile {
                canBeConsumed = false
                attributes.attribute(Usage.USAGE_ATTRIBUTE, cppApiUsage)
                extendsFrom implementation
            }

            // incoming link files
            linkDebug {
                canBeConsumed = false
                attributes {
                    attribute(Usage.USAGE_ATTRIBUTE, linkUsage)
                    attribute(CppBinary.DEBUGGABLE_ATTRIBUTE, true)
                    attribute(CppBinary.OPTIMIZED_ATTRIBUTE, false)
                }
                extendsFrom implementation
            }
            linkRelease {
                canBeConsumed = false
                attributes {
                    attribute(Usage.USAGE_ATTRIBUTE, linkUsage)
                    attribute(CppBinary.DEBUGGABLE_ATTRIBUTE, true)
                    attribute(CppBinary.OPTIMIZED_ATTRIBUTE, true)
                }
                extendsFrom implementation
            }
        }

        /*
         * Create some tasks to drive the CMake build
         */
        def tasks = project.tasks

        def cmakeDebug = tasks.create("cmakeDebug", CMake) {
            buildType = "Debug"
            includeDirs.from(project.configurations.cppCompile)
            linkFiles.from(project.configurations.linkDebug)
            variantDir = project.file("${project.buildDir}/debug")
        }

        def cmakeRelease = tasks.create("cmakeRelease", CMake) {
            buildType = "RelWithDebInfo"
            includeDirs.from(project.configurations.cppCompile)
            linkFiles.from(project.configurations.linkRelease)
            variantDir = project.file("${project.buildDir}/release")
        }

        def assembleDebug = tasks.create("assembleDebug", Make) {
            group = "Build"
            description = "Builds the debug binaries"
            generatedBy cmakeDebug
        }

        def assembleRelease = tasks.create("assembleRelease", Make) {
            group = "Build"
            description = "Builds the release binaries"
            generatedBy cmakeRelease
        }

        tasks.assemble.dependsOn assembleDebug
    }
}