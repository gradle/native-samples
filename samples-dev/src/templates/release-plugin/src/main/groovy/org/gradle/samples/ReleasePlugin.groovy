package org.gradle.samples

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project

import java.util.regex.Pattern

/**
 * A sample plugin that produces releases of a Gradle project. Adds a 'release' task that generates the Swift Package
 * Manager manifest for the project and then commits and tags the result. It uses the 'swiftpm-export' plugin to generate
 * the Swift Package Manager manifest.
 *
 * The 'release' task also increments the version in the build.gradle, ready for the next release.
 *
 * Note: because this is just a sample, it only deals with single project builds.
 */
class ReleasePlugin implements Plugin<Project> {
    void apply(Project project) {
        project.pluginManager.apply("swiftpm-export")
        project.tasks.create("release") {
            // Generate the Swift PM manifest prior to commit
            dependsOn project.tasks.generateSwiftPmManifest
            doLast {
                // Commit and tag changes
                project.exec {
                    commandLine = ["git", "add", "Package.swift"]
                }
                project.exec {
                    commandLine = ["git", "commit", "-a", "-m", "version ${project.version}"]
                }
                project.exec {
                    commandLine = ["git", "tag", project.version]
                }

                // Increment the version in the build script, for next release
                def versionPattern = Pattern.compile("(\\d+)\\.(\\d+)\\.(\\d)")
                def matcher = versionPattern.matcher(project.version)
                if (!matcher.matches()) {
                    throw new GradleException("Could not parse project version '${project.version}'")
                }
                def newVersion = matcher.group(1) + "." + ((matcher.group(2) as Integer) + 1) + ".0"
                def buildFileText = project.buildFile.text
                def updatedText = buildFileText.replaceAll("version\\s*=\\s*'${project.version}'", "version = '${newVersion}'")
                if (updatedText == buildFileText) {
                    throw new GradleException("Could not update version in ${project.buildFile.name}")
                }
                project.buildFile.text = updatedText
            }
        }
    }
}