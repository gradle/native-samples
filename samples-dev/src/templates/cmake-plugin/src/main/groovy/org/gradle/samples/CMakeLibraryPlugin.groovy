package org.gradle.samples

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.attributes.Usage
import org.gradle.language.cpp.CppBinary
import org.gradle.samples.tasks.CMake
import org.gradle.samples.tasks.DownloadZipAndUnpack
import org.gradle.samples.tasks.Make

/**
 * A sample plugin that wraps a CMake build with Gradle to take care of dependency management.
 */
class CMakeLibraryPlugin implements Plugin<Project> {
    void apply(Project project) {
        // Apply the base plugin, to define 'clean' task and other things
        project.pluginManager.apply("lifecycle-base")

        // Add a CMake extension to the Gradle model
        def extension = project.extensions.create("cmake", CMakeExtension.class, project.layout, project.objects)

        /*
         * Define some configurations to present the outputs of this build
         * to other Gradle projects.
         */
        def cppApiUsage = project.objects.named(Usage.class, Usage.C_PLUS_PLUS_API)
        def linkUsage = project.objects.named(Usage.class, Usage.NATIVE_LINK)
        def runtimeUsage = project.objects.named(Usage.class, Usage.NATIVE_RUNTIME)

        project.configurations {
            // dependencies of the library
            implementation {
                canBeConsumed = false
                canBeResolved = false
            }

            // incoming compile time headers - this represents the headers we consume
            cppCompile {
                canBeConsumed = false
                extendsFrom implementation
                attributes.attribute(Usage.USAGE_ATTRIBUTE, cppApiUsage)
            }

            // incoming linktime libraries (i.e. static libraries) - this represents the libraries we consume
            cppLinkDebug {
                canBeConsumed = false
                extendsFrom implementation
                attributes {
                    attribute(Usage.USAGE_ATTRIBUTE, linkUsage)
                    attribute(CppBinary.DEBUGGABLE_ATTRIBUTE, true)
                    attribute(CppBinary.OPTIMIZED_ATTRIBUTE, false)
                }
            }
            cppLinkRelease {
                canBeConsumed = false
                extendsFrom implementation
                attributes {
                    attribute(Usage.USAGE_ATTRIBUTE, linkUsage)
                    attribute(CppBinary.DEBUGGABLE_ATTRIBUTE, true)
                    attribute(CppBinary.OPTIMIZED_ATTRIBUTE, true)
                }
            }

            // incoming runtime libraries (i.e. shared libraries) - this represents the libraries we consume
            cppRuntimeDebug {
                canBeConsumed = false
                extendsFrom implementation
                attributes {
                    attribute(Usage.USAGE_ATTRIBUTE, runtimeUsage)
                    attribute(CppBinary.DEBUGGABLE_ATTRIBUTE, true)
                    attribute(CppBinary.OPTIMIZED_ATTRIBUTE, false)
                }
            }
            cppRuntimeRelease {
                canBeConsumed = false
                extendsFrom implementation
                attributes {
                    attribute(Usage.USAGE_ATTRIBUTE, runtimeUsage)
                    attribute(CppBinary.DEBUGGABLE_ATTRIBUTE, true)
                    attribute(CppBinary.OPTIMIZED_ATTRIBUTE, true)
                }
            }

            // outgoing public headers - this represents the headers we expose (including transitive headers)
            headers {
                canBeResolved = false
                extendsFrom implementation
                attributes.attribute(Usage.USAGE_ATTRIBUTE, cppApiUsage)
            }

            // outgoing linktime libraries (i.e. static libraries) - this represents the libraries we expose (including transitive headers)
            linkDebug {
                canBeResolved = false
                extendsFrom implementation
                attributes {
                    attribute(Usage.USAGE_ATTRIBUTE, linkUsage)
                    attribute(CppBinary.DEBUGGABLE_ATTRIBUTE, true)
                    attribute(CppBinary.OPTIMIZED_ATTRIBUTE, false)
                }
            }
            linkRelease {
                canBeResolved = false
                extendsFrom implementation
                attributes {
                    attribute(Usage.USAGE_ATTRIBUTE, linkUsage)
                    attribute(CppBinary.DEBUGGABLE_ATTRIBUTE, true)
                    attribute(CppBinary.OPTIMIZED_ATTRIBUTE, true)
                }
            }

            // outgoing runtime libraries (i.e. shared libraries) - this represents the libraries we expose (including transitive headers)
            runtimeDebug {
                canBeResolved = false
                extendsFrom implementation
                attributes {
                    attribute(Usage.USAGE_ATTRIBUTE, runtimeUsage)
                    attribute(CppBinary.DEBUGGABLE_ATTRIBUTE, true)
                    attribute(CppBinary.OPTIMIZED_ATTRIBUTE, false)
                }
            }
            runtimeRelease {
                canBeResolved = false
                extendsFrom implementation
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
            linkFiles.from(project.configurations.cppLinkDebug)
            variantDirectory = project.file("${project.buildDir}/debug")
            projectDirectory = extension.projectDirectory
        }

        def cmakeRelease = tasks.create("cmakeRelease", CMake) {
            buildType = "RelWithDebInfo"
            includeDirs.from(project.configurations.cppCompile)
            linkFiles.from(project.configurations.cppLinkDebug)
            variantDirectory = project.file("${project.buildDir}/release")
            projectDirectory = extension.projectDirectory
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

        tasks.assemble.dependsOn assembleDebug

        tasks.withType(CMake) {
            dependsOn tasks.withType(DownloadZipAndUnpack)
        }

        /*
         * Configure the artifacts which should be exposed by this build
         * to other Gradle projects. (Note that this build does not currently
         * expose any runtime (shared library) artifacts)
         */
        def configurations = project.configurations
        configurations.headers.outgoing.artifact(extension.includeDirectory) {
            builtBy tasks.withType(DownloadZipAndUnpack)
        }
        configurations.linkDebug.outgoing.artifact assembleDebug.binary
        configurations.linkRelease.outgoing.artifact assembleRelease.binary
    }
}