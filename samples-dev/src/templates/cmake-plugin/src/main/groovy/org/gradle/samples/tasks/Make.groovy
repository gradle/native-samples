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

package org.gradle.samples.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ListProperty

class Make extends DefaultTask {
    @Internal DirectoryProperty variantDir
    @InputFiles FileCollection makeFiles
    @OutputDirectory DirectoryProperty outputDir
    Provider<File> binary
    ListProperty<String> arguments

    Make() {
        variantDir = newInputDirectory()
        outputDir = newInputDirectory()
        binary = newOutputFile()
        arguments = getProject().getObjects().listProperty(String.class)
    }

    @TaskAction
    void executeMake() {
        def makeExecutable = System.getenv('MAKE_EXECUTABLE') ?: 'make'
        project.exec {
            workingDir variantDir

            List<String> allArguments = [makeExecutable]
            allArguments.addAll(arguments.get())
            commandLine allArguments
        }
    }

    void generatedBy(CMake cmake) {
        variantDir.set(cmake.variantDirectory)
        outputDir.set(variantDir)
        makeFiles = cmake.cmakeFiles
    }

    void generatedBy(ConfigureTask configureTask) {
        variantDir.set(configureTask.makeDir)
        outputDir.set(configureTask.prefixDir)
        makeFiles = configureTask.outputs.files
    }

    void binary(Provider<String> path) {
        binary.set(outputDir.file(path))
    }
}