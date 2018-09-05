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
import org.gradle.samples.tasks.Make
/**
 * A sample plugin that wraps a CMake build with Gradle to take care of dependency management.
 */
class CMakeApplicationPlugin implements Plugin<Project> {
    void apply(Project project) {
        project.pluginManager.apply("org.gradle.samples.wrapped-native-application")

        /*
         * Create some tasks to drive the CMake build
         */
        def tasks = project.tasks

        def cmakeDebug = tasks.create("cmakeDebug", CMake) {
            buildType = "Debug"
            includeDirs.from(project.configurations.cppCompile)
            linkFiles.from(project.configurations.linkDebug)
            variantDirectory = project.file("${project.buildDir}/debug")
            projectDirectory = project.layout.projectDirectory
        }

        def cmakeRelease = tasks.create("cmakeRelease", CMake) {
            buildType = "RelWithDebInfo"
            includeDirs.from(project.configurations.cppCompile)
            linkFiles.from(project.configurations.linkRelease)
            variantDirectory = project.file("${project.buildDir}/release")
            projectDirectory = project.layout.projectDirectory
        }

        def assembleDebug = tasks.create("assembleDebug", Make) {
            group = "Build"
            description = "Builds the debug binaries"
            generatedBy cmakeDebug
            binary project.provider { project.name }
        }

        def assembleRelease = tasks.create("assembleRelease", Make) {
            group = "Build"
            description = "Builds the release binaries"
            generatedBy cmakeRelease
            binary project.provider { project.name }
        }

        tasks.assemble.dependsOn assembleDebug
    }
}