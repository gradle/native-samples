package org.gradle.samples.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.gradle.api.provider.Provider

class Make extends DefaultTask {
    @Internal DirectoryProperty variantDir
    @InputFiles FileCollection cmakeFiles
    Provider<File> binary

    Make() {
        variantDir = newInputDirectory()
        binary = newOutputFile()
    }

    @TaskAction
    void executeMake() {
        def makeExecutable = System.getenv('MAKE_EXECUTABLE') ?: 'make'
        project.exec {
            workingDir variantDir

            commandLine makeExecutable
        }
    }

    void generatedBy(CMake cmake) {
        variantDir.set(cmake.variantDirectory)
        cmakeFiles = cmake.cmakeFiles
    }

    void binary(Provider<String> path) {
        binary.set(variantDir.file(path))
    }
}