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
import org.gradle.api.tasks.Copy
import org.gradle.samples.tasks.*

class IOSApplicationPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        project.with {
            plugins.withId('swift-application') {
                def compileStoryboardTask = tasks.register("compileStoryboard", StoryboardCompile) {
                    module = application.module
                    outputDirectory = layout.buildDirectory.dir("ios/storyboards/compiled")
                    partialPropertyListOutputDirectory = layout.buildDirectory.dir("ios/partial-plist/storyboards")
                }

                def linkStoryboardTask = tasks.register("linkStoryboard", StoryboardLink) {
                    module = application.module
                    sources.from compileStoryboardTask.flatMap { it.outputDirectory }
                    outputDirectory = layout.buildDirectory.dir("ios/storyboards/linked")
                }

                def compileAssetCatalogTask = tasks.register("compileAssetCatalog", AssetCatalogCompile) {
                    identifier = provider { "${project.group}.${application.module.get()}".toString() }
                    partialPropertyListOutputDirectory = layout.buildDirectory.dir("ios/partial-plist/asset-catalogs")
                    outputDirectory = layout.buildDirectory.dir("ios/assert-catalogs")
                }

                def mergePropertyListTask = tasks.register("mergePropertyList", MergePropertyList) {
                    sources.from compileStoryboardTask.map { it.partialPropertyListOutputDirectory.asFileTree }
                    sources.from compileAssetCatalogTask.map { it.partialPropertyListOutputDirectory.asFileTree }
                    outputFile = layout.buildDirectory.file("ios/Info.plist")
                    module = application.module
                    identifier = provider { "${project.group}.${application.module.get()}".toString() }
                }

                def createPackageInformationTask = tasks.register("createPackageInformation", CreatePackageInformation) {
                    outputFile = layout.buildDirectory.file("ios/PkgInfo")
                }

                def createEntitlementTask = tasks.register("createEntitlement", CreateEntitlement) {
                    identifier = provider { "${project.group}.${application.module.get()}".toString() }
                    outputFile = layout.buildDirectory.file(provider { "ios/entitlements/${application.module.get()}.app.xcent" })
                }

                application.binaries.configureEach { binary ->
                    compileTask.get().compilerArgs.addAll provider {
                        ["-target", "x86_64-apple-ios11.2", "-sdk", "xcrun --sdk iphonesimulator --show-sdk-path".execute().text.trim()/*, "-enforce-exclusivity=checked"*/]
                    }

                    linkTask.get().inputs.file createEntitlementTask.flatMap { it.outputFile }
                    linkTask.get().linkerArgs.addAll provider {
                        ["-target", "x86_64-apple-ios11.2", "-sdk", "xcrun --sdk iphonesimulator --show-sdk-path".execute().text.trim(), "-Xlinker", "-rpath", "-Xlinker", "@executable_path/Frameworks", "-Xlinker", "-export_dynamic", "-Xlinker", "-no_deduplicate", "-Xlinker", "-objc_abi_version", "-Xlinker", "2", "-Xlinker", "-sectcreate", "-Xlinker", "__TEXT", "-Xlinker", "__entitlements", "-Xlinker", createEntitlementTask.get().outputFile.get().asFile.absolutePath]
                    }
                }

                def installApplicationBundleDebugTask = tasks.register("installApplicationBundleDebug", InstallApplicationBundle) {
                    applicationBundle = layout.buildDirectory.file(provider { "ios/products/debug/${application.module.get()}.app" })
                    sources.from(mergePropertyListTask.flatMap { it.outputFile })
                    sources.from(createPackageInformationTask.flatMap { it.outputFile })
                    sources.from(compileAssetCatalogTask.flatMap { it.outputDirectory })
                    sources.from(linkStoryboardTask.flatMap { it.outputDirectory })
                }
                tasks.matching({ it.name == 'linkDebug' }).all { linkTask ->
                    installApplicationBundleDebugTask.configure { it.executableFile = linkTask.linkedFile }
                }

                tasks.matching({ it.name == "installDebug" }).all {
                    dependsOn installApplicationBundleDebugTask
                    enabled = false
                }

                def installApplicationBundleReleaseTask = tasks.register("installApplicationBundleRelease", InstallApplicationBundle) {
                    applicationBundle = layout.buildDirectory.file(provider { "ios/products/release/${application.module.get()}.app" })
                    sources.from(mergePropertyListTask.flatMap { it.outputFile })
                    sources.from(createPackageInformationTask.flatMap { it.outputFile })
                    sources.from(compileAssetCatalogTask.flatMap { it.outputDirectory })
                    sources.from(linkStoryboardTask.flatMap { it.outputDirectory })
                }
                tasks.matching({ it.name == 'linkRelease' }).all { linkTask ->
                    installApplicationBundleReleaseTask.configure { it.executableFile = linkTask.linkedFile }
                }

                tasks.matching({ it.name == "installRelease" }).all {
                    dependsOn installApplicationBundleReleaseTask
                    enabled = false
                }

                // Configure iOS specific task source location convention
                mergePropertyListTask.configure { sources.from layout.projectDirectory.file("src/main/resources/Info.plist") }
                compileStoryboardTask.configure { sources.from fileTree(dir: 'src/main/resources', includes: ['*.lproj/*.storyboard']) }
                compileAssetCatalogTask.configure { source = layout.projectDirectory.file("src/main/resources/Assets.xcassets") }
            }
        }
    }
}
