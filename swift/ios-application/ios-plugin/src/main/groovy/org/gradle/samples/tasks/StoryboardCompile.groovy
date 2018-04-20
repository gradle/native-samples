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
    final DirectoryProperty partialPropertyListOutputDirectory = newOutputDirectory()

    @OutputDirectory
    final DirectoryProperty outputDirectory = newOutputDirectory()

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