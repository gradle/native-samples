package org.gradle.samples

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.Input
import org.gradle.api.provider.Property
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

class DownloadZipAndUnpack extends DefaultTask {
    @Input Property<String> url
    @OutputDirectory final DirectoryProperty outputDirectory

    DownloadZipAndUnpack() {
        url = project.objects.property(String)
        outputDirectory = newOutputDirectory()
        outputDirectory.set(project.layout.buildDirectory.dir(project.name))
        onlyIf { url.isPresent() }
    }

    @TaskAction
    void doDownloadZipAndUnpack() {
        def downloadUrl = new URL(url.get())
        logger.warn("Downloading $url")
        def zipDestination = new File(temporaryDir, "zip.zip")
        downloadUrl.withInputStream { zipBytes ->
            zipDestination.withOutputStream { it << zipBytes }
        }
        logger.warn("Downloaded to ${zipDestination.absolutePath}")

        def unzipDestination = outputDirectory.get().asFile
        ant.unzip(src: zipDestination, dest: unzipDestination)
    }
}
