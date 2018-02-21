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

package org.gradle.samples.plugins.cmake

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.samples.tasks.CMake
import org.gradle.samples.tasks.DownloadZipAndUnpack
import org.gradle.samples.tasks.Make
/**
 * A sample plugin that wraps a CMake build with Gradle to take care of dependency management.
 */
class CMakeLibraryPlugin implements Plugin<Project> {
    void apply(Project project) {
        project.pluginManager.apply("org.gradle.samples.wrapped-native-library")

        // Add a CMake extension to the Gradle model
        def extension = project.extensions.create("cmake", CMakeExtension.class, project.layout, project.objects)

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

        /*
         * Configure the artifacts which should be exposed by this build
         * to other Gradle projects. (Note that this build does not currently
         * expose any runtime (shared library) artifacts)
         */
        def configurations = project.configurations
        configurations.headers.outgoing.artifact extension.includeDirectory
        configurations.linkDebug.outgoing.artifact assembleDebug.binary
        configurations.linkRelease.outgoing.artifact assembleRelease.binary
    }
}