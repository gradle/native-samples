package org.gradle.samples

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.attributes.Usage
import org.gradle.language.cpp.CppBinary

/**
 * A sample plugin that wraps a CMake build with Gradle to take care of dependency management.*/
class CMakePlugin implements Plugin<Project> {
    void apply(Project project) {
        // Apply the base plugin, to define 'clean' task and other things
        project.pluginManager.apply("base")

        // Add a CMake extension to the Gradle model
        def extension = project.extensions.create("cmake", CMakeExtension.class, project.objects)

        /*
         * Define some configurations to present the outputs of this build
         * to other Gradle projects.
         */
        def cppApiUsage = project.objects.named(Usage.class, Usage.C_PLUS_PLUS_API)
        def linkUsage = project.objects.named(Usage.class, Usage.NATIVE_LINK)
        def runtimeUsage = project.objects.named(Usage.class, Usage.NATIVE_RUNTIME)

        project.configurations {
            // outgoing public headers
            headers {
                canBeResolved = false
                attributes.attribute(Usage.USAGE_ATTRIBUTE, cppApiUsage)
            }

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

            // outgoing linktime libraries (i.e. static libraries)
            linktimeLibsDebug {
                canBeResolved = false
                attributes {
                    attribute(Usage.USAGE_ATTRIBUTE, linkUsage)
                    attribute(CppBinary.DEBUGGABLE_ATTRIBUTE, true)
                    attribute(CppBinary.OPTIMIZED_ATTRIBUTE, false)
                }
            }
            linktimeLibsRelease {
                canBeResolved = false
                attributes {
                    attribute(Usage.USAGE_ATTRIBUTE, linkUsage)
                    attribute(CppBinary.DEBUGGABLE_ATTRIBUTE, true)
                    attribute(CppBinary.OPTIMIZED_ATTRIBUTE, true)
                }
            }

            // outgoing runtime libraries (i.e. shared libraries)
            runtimeLibsDebug {
                canBeResolved = false
                attributes {
                    attribute(Usage.USAGE_ATTRIBUTE, runtimeUsage)
                    attribute(CppBinary.DEBUGGABLE_ATTRIBUTE, true)
                    attribute(CppBinary.OPTIMIZED_ATTRIBUTE, false)
                }
            }
            runtimeLibsRelease {
                canBeResolved = false
                attributes {
                    attribute(Usage.USAGE_ATTRIBUTE, runtimeUsage)
                    attribute(CppBinary.DEBUGGABLE_ATTRIBUTE, true)
                    attribute(CppBinary.OPTIMIZED_ATTRIBUTE, true)
                }
            }
        }

        /*
         * Create some tasks to drive the CMake build
         */
        def tasks = project.tasks

        def cmakeDebug = tasks.create("cmakeDebug", CMake) {
            buildType = "Debug"
            includeDirs.from(project.configurations.cppCompile)
            variantDir = project.file("${project.buildDir}/debug")
        }

        def cmakeRelease = tasks.create("cmakeRelease", CMake) {
            buildType = "RelWithDebInfo"
            includeDirs.from(project.configurations.cppCompile)
            variantDir = project.file("${project.buildDir}/release")
        }

        def assembleDebug = tasks.create("assembleDebug", Make) {
            group = "Build"
            description = "Builds the debug binaries"
            generatedBy cmakeDebug
            binary extension.binary
        }

        def assembleRelease = tasks.create("assembleRelease", Make) {
            group = "Build"
            description = "Builds the release binaries"
            generatedBy cmakeRelease
            binary extension.binary
        }

        /*
         * Configure the artifacts which should be exposed by this build
         * to other Gradle projects. (Note that this build does not currently
         * expose any runtime (shared library) artifacts)
         */
        def configurations = project.configurations
        configurations.headers.outgoing.artifact project.layout.projectDirectory.dir(extension.includeDir)
        configurations.linktimeLibsDebug.outgoing.artifact tasks.assembleDebug.binary
        configurations.linktimeLibsRelease.outgoing.artifact tasks.assembleRelease.binary
    }
}