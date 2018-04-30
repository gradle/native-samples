package org.gradle.samples.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

@CacheableTask
class AssetCatalogCompile extends DefaultTask {
    @InputDirectory
    final RegularFileProperty source = newInputFile()

    @Input
    final Property<String> identifier = project.objects.property(String)

    @OutputDirectory
    final DirectoryProperty partialPropertyListOutputDirectory = newOutputDirectory()

    @OutputDirectory
    final DirectoryProperty outputDirectory = newOutputDirectory()

    @TaskAction
    private void doCompile() {
        project.exec {
            executable actoolExecutable.absolutePath
            args "--output-format", "human-readable-text", "--notices", "--warnings", "--export-dependency-info", "$temporaryDir/assetcatalog_dependencies", "--output-partial-info-plist", "${partialPropertyListOutputDirectory.get().asFile.absolutePath}/assetcatalog_generated_info.plist", "--app-icon", "AppIcon", "--compress-pngs", "--enable-on-demand-resources", "YES", "--filter-for-device-model", "iPhone10,5", "--filter-for-device-os-version", "11.2", "--sticker-pack-identifier-prefix", "${identifier.get()}.sticker-pack.", "--target-device", "iphone", "--target-device", "ipad", "--minimum-deployment-target", "11.2", "--platform", "iphonesimulator", "--product-type", "com.apple.product-type.application", "--compile", outputDirectory.get().asFile.getAbsoluteFile(), source.get().asFile.absolutePath
            standardOutput = new FileOutputStream(project.file("$temporaryDir/outputs.txt"))
        }
    }

    @InputFile
    protected File getActoolExecutable() {
        return new File("xcrun --sdk iphonesimulator --find actool".execute().text.trim())
    }
}
