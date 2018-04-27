/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gradle.samples.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.ModuleVersionIdentifier
import org.gradle.api.attributes.Usage
import org.gradle.api.component.ComponentWithVariants
import org.gradle.api.internal.artifacts.DefaultModuleVersionIdentifier
import org.gradle.api.internal.artifacts.publish.ArchivePublishArtifact
import org.gradle.api.internal.component.UsageContext
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.bundling.Zip
import org.gradle.language.cpp.CppBinary
import org.gradle.language.cpp.internal.MainLibraryVariant
import org.gradle.language.nativeplatform.internal.PublicationAwareComponent
import org.gradle.language.plugins.NativeBasePlugin
import org.gradle.samples.WrappedUsageContext
import org.gradle.samples.WrappedPublishableComponent

class WrappedNativeLibraryPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        project.pluginManager.apply("org.gradle.samples.wrapped-native-base")

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

        // Using the publication generation logic from Gradle
        project.plugins.apply(NativeBasePlugin)

        /*
        TODO: We need to detangle this from the built-in plugins so that external plugins can opt into this same behavior
         */
        // Create components expected by the native Gradle publication code
        PublicationAwareComponent mainComponent = new PublicationAwareComponent() {
            MainLibraryVariant mainVariant = new MainLibraryVariant("api", cppApiUsage, project.configurations.headers)
            @Override
            Provider<String> getBaseName() {
                return project.provider { project.name }
            }

            @Override
            ComponentWithVariants getMainPublication() {
                return mainVariant
            }

            @Override
            String getName() {
                return "main"
            }
        }
        WrappedPublishableComponent debugVariant = new WrappedPublishableComponent() {
            @Override
            ModuleVersionIdentifier getCoordinates() {
                return new DefaultModuleVersionIdentifier(project.group.toString(), project.name + "_" + name, project.version.toString())
            }

            @Override
            Set<? extends UsageContext> getUsages() {
                Set<UsageContext> result = new HashSet<UsageContext>()
                result.add(new WrappedUsageContext('debugLink', linkUsage, project.configurations.linkDebug))
                result.add(new WrappedUsageContext('debugRuntime', runtimeUsage, project.configurations.runtimeDebug))
                return result;
            }

            @Override
            String getName() {
                return "mainDebug"
            }
        }
        WrappedPublishableComponent releaseVariant = new WrappedPublishableComponent() {
            @Override
            ModuleVersionIdentifier getCoordinates() {
                return new DefaultModuleVersionIdentifier(project.group.toString(), project.name + "_" + name, project.version.toString())
            }

            @Override
            Set<? extends UsageContext> getUsages() {
                Set<UsageContext> result = new HashSet<UsageContext>()
                result.add(new WrappedUsageContext('releaseLink', linkUsage, project.configurations.linkRelease))
                result.add(new WrappedUsageContext('releaseRuntime', runtimeUsage, project.configurations.runtimeRelease))
                return result
            }

            @Override
            String getName() {
                return "mainRelease"
            }
        }

        mainComponent.mainPublication.addVariant(debugVariant)
        mainComponent.mainPublication.addVariant(releaseVariant)

        Zip headersZip = project.tasks.create("cppHeaders", Zip) {
            from project.configurations.headers.artifacts.files.asFileTree
            // TODO - should track changes to build directory
            destinationDir = new File(project.getBuildDir(), "headers")
            classifier = "cpp-api-headers"
            archiveName = "cpp-api-headers.zip"
        }
        mainComponent.mainPublication.addArtifact(new ArchivePublishArtifact(headersZip))

        project.components.add(mainComponent)
    }
}
