package org.gradle.samples.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.*

@CacheableTask
class InstallApplicationBundle extends DefaultTask {
    @InputFiles
    final ConfigurableFileCollection sources = project.files()

    @InputFile
    final RegularFileProperty executableFile = newInputFile()

    @OutputDirectory
    final RegularFileProperty applicationBundle = newOutputFile()

    @TaskAction
    private void doInstall() {
        project.copy {
            into applicationBundle

            for (File source : sources) {
                from(source)
            }
        }

        File bundleDir = applicationBundle.get().asFile

        project.exec {
            workingDir bundleDir
            executable swiftStdlibToolExecutable.absolutePath
            args "--copy", "--sign", "-",
                    "--scan-executable", executableFile.get().asFile.absolutePath,
                    "--destination", new File(bundleDir, "Frameworks").absolutePath,
                    "--platform", "iphonesimulator",
                    "--resource-destination", bundleDir.absolutePath,
                    "--scan-folder", new File(bundleDir, "Frameworks").absolutePath,
                    "--scan-folder", new File(bundleDir, "PlugIns").absolutePath,
                    "--strip-bitcode",
                    "--resource-library", "libswiftRemoteMirror.dylib"
        }.assertNormalExitValue()

        project.exec {
            executable codesignExecutable.absolutePath
            args "--force", "--sign", "-", "--timestamp=none", bundleDir.absolutePath
        }.assertNormalExitValue()
    }

    @InputFile
    protected File getSwiftStdlibToolExecutable() {
        return new File("xcrun --sdk iphonesimulator --find swift-stdlib-tool".execute().text.trim())
    }

    @InputFile
    protected File getCodesignExecutable() {
        return new File("xcrun --sdk iphonesimulator --find codesign".execute().text.trim())
    }
}
