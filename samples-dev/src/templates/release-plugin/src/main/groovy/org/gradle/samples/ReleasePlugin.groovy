package org.gradle.samples

import org.gradle.api.*

class ReleasePlugin implements Plugin<Project> {
    void apply(Project project) {
        project.pluginManager.apply("swiftpm-export")
        project.tasks.create("release") {
            dependsOn project.tasks.generateSwiftPmManifest
            doLast {
                project.exec {
                    commandLine = ["git", "add", "Package.swift"]
                }
                project.exec {
                    commandLine = ["git", "commit", "-a", "-m", "version ${project.version}"]
                }
                project.exec {
                    commandLine = ["git", "tag", project.version]
                }
            }
        }
    }
}