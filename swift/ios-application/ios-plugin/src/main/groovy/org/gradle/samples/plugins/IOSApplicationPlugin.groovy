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
                def compileStoryboardTask = tasks.create("compileStoryboard", StoryboardCompile) {
                    module = application.module
                    outputDirectory = layout.buildDirectory.dir(project.provider { "ios/storyboards/compiled" })
                    partialPropertyListOutputDirectory = layout.buildDirectory.dir("ios/partial-plist/storyboards")
                }

                def linkStoryboardTask = tasks.create("linkStoryboard", StoryboardLink) {
                    module = application.module
                    sources.from compileStoryboardTask.outputDirectory
                    outputDirectory = layout.buildDirectory.dir("ios/storyboards/linked")
                }

                def compileAssetCatalog = tasks.create("compileAssetCatalog", AssetCatalogCompile) {
                    identifier = provider { "${project.group}.${application.module.get()}".toString() }
                    partialPropertyListOutputDirectory = layout.buildDirectory.dir("ios/partial-plist/asset-catalogs")
                    outputDirectory = layout.buildDirectory.dir("ios/assert-catalogs")
                }

                def mergePropertyListTask = tasks.create("mergePropertyList", MergePropertyList) {
                    sources.from compileStoryboardTask.partialPropertyListOutputDirectory.asFileTree
                    sources.from compileAssetCatalog.partialPropertyListOutputDirectory.asFileTree
                    outputFile = layout.buildDirectory.file("ios/Info.plist")
                    module = application.module
                    identifier = provider { "${project.group}.${application.module.get()}".toString() }
                }

                def createPackageInformationTask = tasks.create("createPackageInformation", CreatePackageInformation) {
                    outputFile = layout.buildDirectory.file("ios/PkgInfo")
                }

                def createEntitlementTask = tasks.create("createEntitlement", CreateEntitlement) {
                    identifier = provider { "${project.group}.${application.module.get()}".toString() }
                    outputFile = layout.buildDirectory.file(provider { "ios/entitlements/${application.module.get()}.app.xcent" })
                }

                application.binaries.configureEach { binary ->
                    compileTask.get().compilerArgs.addAll provider {
                        ["-target", "x86_64-apple-ios11.2", "-sdk", "xcrun --sdk iphonesimulator --show-sdk-path".execute().text.trim()/*, "-enforce-exclusivity=checked"*/]
                    }

                    linkTask.get().dependsOn createEntitlementTask
                    linkTask.get().inputs.file createEntitlementTask.outputFile
                    linkTask.get().linkerArgs.addAll provider {
                        ["-target", "x86_64-apple-ios11.2", "-sdk", "xcrun --sdk iphonesimulator --show-sdk-path".execute().text.trim(), "-Xlinker", "-rpath", "-Xlinker", "@executable_path/Frameworks", "-Xlinker", "-export_dynamic", "-Xlinker", "-no_deduplicate", "-Xlinker", "-objc_abi_version", "-Xlinker", "2", "-Xlinker", "-sectcreate", "-Xlinker", "__TEXT", "-Xlinker", "__entitlements", "-Xlinker", createEntitlementTask.outputFile.get().asFile.absolutePath]
                    }
                }

                def installApplicationBundleDebugTask = tasks.create("installApplicationBundleDebug", InstallApplicationBundle) {
                    applicationBundle = layout.buildDirectory.file(provider { "ios/products/debug/${application.module.get()}.app" })
                    sources.from(mergePropertyListTask.outputFile)
                    sources.from(createPackageInformationTask.outputFile)
                    sources.from(compileAssetCatalog.outputDirectory)
                    sources.from(linkStoryboardTask.outputDirectory)
                    tasks.matching({ it.name == 'linkDebug' }).all {
                        executableFile = it.linkedFile
                    }
                }

                tasks.matching({ it.name == "installDebug" }).all {
                    dependsOn installApplicationBundleDebugTask
                    enabled = false
                }

                def installApplicationBundleReleaseTask = tasks.create("installApplicationBundleRelease", InstallApplicationBundle) {
                    applicationBundle = layout.buildDirectory.file(provider { "ios/products/release/${application.module.get()}.app" })
                    sources.from(mergePropertyListTask.outputFile)
                    sources.from(createPackageInformationTask.outputFile)
                    sources.from(compileAssetCatalog.outputDirectory)
                    sources.from(linkStoryboardTask.outputDirectory)
                    tasks.matching({ it.name == 'linkRelease' }).all {
                        executableFile = it.linkedFile
                    }
                }

                tasks.matching({ it.name == "installRelease" }).all {
                    dependsOn installApplicationBundleReleaseTask
                    enabled = false
                }
            }
        }
    }
}
