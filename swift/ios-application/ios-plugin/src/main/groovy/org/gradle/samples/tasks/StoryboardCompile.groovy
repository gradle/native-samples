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

import org.apache.commons.io.FilenameUtils
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*

@CacheableTask
class StoryboardCompile extends DefaultTask {
    @Input
    final Property<String> module = project.objects.property(String)

    @InputFiles
    final ConfigurableFileCollection sources = project.files()

    @OutputDirectory
    final DirectoryProperty partialPropertyListOutputDirectory = project.objects.directoryProperty()

    @OutputDirectory
    final DirectoryProperty outputDirectory = project.objects.directoryProperty()

    @TaskAction
    private void doCompile() {
        def ibtoolExecutable = ibtoolExecutable.absolutePath

        project.file("$temporaryDir/outputs.txt").delete()
        for (File source : sources) {
            project.exec {
                executable ibtoolExecutable
                args "--errors", "--warnings", "--notices", "--module", module.get(), "--output-partial-info-plist", "${partialPropertyListOutputDirectory.get().asFile.absolutePath}/${FilenameUtils.removeExtension(source.getName())}-SBPartialInfo.plist", "--auto-activate-custom-fonts", "--target-device", "iphone", "--target-device", "ipad", "--minimum-deployment-target", "11.2", "--output-format", "human-readable-text", "--compilation-directory", outputDirectory.get().asFile.absolutePath + "/" + source.parentFile.name, source.absolutePath
                standardOutput = new FileOutputStream(project.file("$temporaryDir/outputs.txt"), true)
            }
        }
    }

    @InputFile
    protected File getIbtoolExecutable() {
        return new File("xcrun --sdk iphonesimulator --find ibtool".execute().text.trim())
    }
}