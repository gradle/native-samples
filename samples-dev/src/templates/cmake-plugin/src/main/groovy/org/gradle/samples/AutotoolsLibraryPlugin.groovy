package org.gradle.samples

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.attributes.Usage
import org.gradle.language.cpp.CppBinary
import org.gradle.samples.tasks.ConfigureTask
import org.gradle.samples.tasks.Make

class AutotoolsLibraryPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        project.pluginManager.apply("lifecycle-base")

        def extension = project.extensions.create("autotools", AutotoolsExtension.class, project.layout, project.objects)

        def configureDebug = project.tasks.create('configureDebug', ConfigureTask) {
            sourceDir.set extension.sourceDirectory
            prefixDir.set project.layout.buildDirectory.dir("debug")
            makeDir.set project.layout.buildDirectory.dir("make-debug")

            arguments.addAll(extension.configureArguments)
            arguments.add("--enable-shared=no")
            arguments.add("--enable-debug")
        }

        def assembleDebug = project.tasks.create('assembleDebug', Make) {
            generatedBy configureDebug
            binary extension.binary

            arguments.addAll(extension.makeArguments)
            arguments.add("install")
        }

        def configureRelease = project.tasks.create('configureRelease', ConfigureTask) {
            sourceDir.set extension.sourceDirectory
            prefixDir.set project.layout.buildDirectory.dir("release")
            makeDir.set project.layout.buildDirectory.dir("make-release")

            arguments.addAll(extension.configureArguments)
            arguments.add("--enable-shared=no")
            arguments.add("--enable-debug")
            arguments.add("--enable-optimizations")
        }

        def assembleRelease = project.tasks.create('assembleRelease', Make) {
            generatedBy configureRelease
            binary extension.binary

            arguments.addAll(extension.makeArguments)
            arguments.add("install")
        }

        def cppApiUsage = project.objects.named(Usage.class, Usage.C_PLUS_PLUS_API)
        def linkUsage = project.objects.named(Usage.class, Usage.NATIVE_LINK)
        def runtimeUsage = project.objects.named(Usage.class, Usage.NATIVE_RUNTIME)

        project.configurations {
            // outgoing public headers - this represents the headers we expose (including transitive headers)
            headers {
                canBeResolved = false
                attributes.attribute(Usage.USAGE_ATTRIBUTE, cppApiUsage)
            }

            // outgoing linktime libraries (i.e. static libraries) - this represents the libraries we expose (including transitive headers)
            linkDebug {
                canBeResolved = false
                attributes {
                    attribute(Usage.USAGE_ATTRIBUTE, linkUsage)
                    attribute(CppBinary.DEBUGGABLE_ATTRIBUTE, true)
                    attribute(CppBinary.OPTIMIZED_ATTRIBUTE, false)
                }
            }
            linkRelease {
                canBeResolved = false
                attributes {
                    attribute(Usage.USAGE_ATTRIBUTE, linkUsage)
                    attribute(CppBinary.DEBUGGABLE_ATTRIBUTE, true)
                    attribute(CppBinary.OPTIMIZED_ATTRIBUTE, true)
                }
            }

            // outgoing runtime libraries (i.e. shared libraries) - this represents the libraries we expose (including transitive headers)
            runtimeDebug {
                canBeResolved = false
                attributes {
                    attribute(Usage.USAGE_ATTRIBUTE, runtimeUsage)
                    attribute(CppBinary.DEBUGGABLE_ATTRIBUTE, true)
                    attribute(CppBinary.OPTIMIZED_ATTRIBUTE, false)
                }
            }
            runtimeRelease {
                canBeResolved = false
                attributes {
                    attribute(Usage.USAGE_ATTRIBUTE, runtimeUsage)
                    attribute(CppBinary.DEBUGGABLE_ATTRIBUTE, true)
                    attribute(CppBinary.OPTIMIZED_ATTRIBUTE, true)
                }
            }
        }

        project.configurations.headers.outgoing.artifact extension.includeDirectory
        project.configurations.linkDebug.outgoing.artifact assembleDebug.binary
        project.configurations.linkRelease.outgoing.artifact assembleRelease.binary
    }
}