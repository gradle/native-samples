package org.gradle.samples

import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputFiles
import org.gradle.api.tasks.TaskAction

/**
 * Task types to execute CMake
 */
class CMake extends DefaultTask {
    @Input String buildType
    @Internal DirectoryProperty variantDir
    @InputFiles ConfigurableFileCollection includeDirs
    @InputFiles ConfigurableFileCollection linkFiles

    CMake() {
        variantDir = newOutputDirectory()
        includeDirs = project.files()
        linkFiles = project.files()
    }

    @TaskAction
    void generateCmakeFiles() {
        def cmakeExecutable = System.getenv('CMAKE_EXECUTABLE') ?: 'cmake'

        variantDir.get().asFile.mkdirs()
        project.exec {
            workingDir variantDir.get()
            commandLine cmakeExecutable, "-DCMAKE_BUILD_TYPE=${buildType.capitalize()}", "-DINCLUDE_DIRS=${includeDirs.join(';  ')}", "-DCMAKE_EXE_LINKER_FLAGS=${linkFiles.join(' ')}", project.projectDir
        }
    }

    @InputFiles
    FileCollection getCMakeLists() {
        return project.fileTree(project.projectDir).include('**/CMakeLists.txt')
    }

    @OutputFiles
    FileCollection getCmakeFiles() {
        project.fileTree(variantDir.get())
            .include('**/CMakeFiles/**/*')
            .include('**/Makefile')
            .include('**/*.cmake')
    }
}
