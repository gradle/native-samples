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
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileCollection
import org.gradle.api.model.ObjectFactory
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputFiles
import org.gradle.api.tasks.TaskAction

import javax.inject.Inject

/**
 * Task types to execute CMake
 */
class CMake extends DefaultTask {
    @Input String buildType
    @Internal final DirectoryProperty variantDirectory
    @Internal final DirectoryProperty projectDirectory
    @InputFiles ConfigurableFileCollection includeDirs
    @InputFiles ConfigurableFileCollection linkFiles

    @Inject
    CMake(ObjectFactory objectFactory) {
        variantDirectory = objectFactory.directoryProperty()
        dependsOn(variantDirectory)
        projectDirectory = objectFactory.directoryProperty()
        dependsOn(projectDirectory)

        includeDirs = project.files()
        linkFiles = project.files()
    }

    @TaskAction
    void generateCmakeFiles() {
        def cmakeExecutable = System.getenv('CMAKE_EXECUTABLE') ?: 'cmake'

        variantDirectory.get().asFile.mkdirs()
        project.exec {
            workingDir variantDirectory.get()
            commandLine cmakeExecutable,
                    "-DCMAKE_BUILD_TYPE=${buildType.capitalize()}",
                    "-DINCLUDE_DIRS=${includeDirs.join(';  ')}",
                    "-DLINK_DIRS=${linkFiles.collect { it.parent }.join(';')}",
                    "--no-warn-unused-cli",
                    projectDirectory.get().asFile.absolutePath
        }
    }

    @InputFiles
    FileCollection getCMakeLists() {
        return project.fileTree(projectDirectory.get().asFile).include('**/CMakeLists.txt')
    }

    @OutputFiles
    FileCollection getCmakeFiles() {
        project.fileTree(variantDirectory.get())
            .include('**/CMakeFiles/**/*')
            .include('**/Makefile')
            .include('**/*.cmake')
    }
}
