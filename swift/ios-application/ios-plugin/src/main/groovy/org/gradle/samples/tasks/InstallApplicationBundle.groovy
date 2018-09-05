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
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.*

@CacheableTask
class InstallApplicationBundle extends DefaultTask {
    @InputFiles
    final ConfigurableFileCollection sources = project.files()

    @InputFile
    final RegularFileProperty executableFile = project.objects.fileProperty()

    @OutputDirectory
    final RegularFileProperty applicationBundle = project.objects.fileProperty()

    @TaskAction
    private void doInstall() {
        project.copy {
            into applicationBundle

            from(executableFile)
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
            standardOutput = new FileOutputStream(project.file("$temporaryDir/outputs.txt"))
        }

        project.exec {
            executable codesignExecutable.absolutePath
            args "--force", "--sign", "-", "--timestamp=none", bundleDir.absolutePath
            standardOutput = new FileOutputStream(project.file("$temporaryDir/outputs.txt"), true)
        }
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
