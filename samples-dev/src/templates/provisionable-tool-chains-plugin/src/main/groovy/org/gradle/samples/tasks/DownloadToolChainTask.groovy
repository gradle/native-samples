package org.gradle.samples.tasks

import groovy.transform.CompileStatic
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.compress.utils.IOUtils
import org.apache.commons.io.FilenameUtils
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFiles
import org.gradle.api.tasks.TaskAction

@CompileStatic
class DownloadToolChainTask extends DefaultTask {
    @Optional
    @Input  // URL is assumed to always point at the same file
    final Property<URL> fromUrl = project.objects.property(URL)

    @Optional
    @Input
    final Property<String> md5 = project.objects.property(String)

    final DirectoryProperty toolChainRepositoryDirectory = project.layout.directoryProperty()

    private final Provider<String> archiveName = getProject().provider { FilenameUtils.getName(fromUrl.get().path) }

    private final RegularFileProperty toolChainArchive = newOutputFile()

    DownloadToolChainTask() {
        toolChainArchive.set(project.provider {
            if (fromUrl.isPresent()) {
                return toolChainRepositoryDirectory.file(archiveName).get()
            }
            return null
        })
    }

    Provider<RegularFile> getToolChainArchive() {
        return toolChainArchive
    }

    @OutputFiles
    protected File getDoneFile() {
        if (fromUrl.present) {
            return toolChainRepositoryDirectory.file("${archiveName.get()}.done").get().asFile
        }
        return null
    }

    @TaskAction
    private void download() {
        if (fromUrl.present && !isArchiveUpToDate()) {
            doneFile.delete()
            toolChainArchiveFile.delete()

            File temporaryFile = new File(temporaryDir, toolChainArchiveFile.getName())
            fromUrl.get().openStream().withCloseable { inStream ->
                new FileOutputStream(temporaryFile).withCloseable { outStream ->
                    IOUtils.copy(inStream, outStream)
                }
            }

            String archiveMd5 = md5Hash(temporaryFile)
            if (md5.present && archiveMd5 != md5.get()) {
                throw new IllegalStateException("MD5 doesn't match!")
            }

            toolChainArchiveFile.parentFile.mkdirs()
            temporaryFile.renameTo(toolChainArchiveFile)
            doneFile.text = archiveMd5
        }
    }

    private boolean isArchiveUpToDate() {
        return toolChainArchiveFile.exists() && doneFile.exists() && doneFile.text == md5Hash(getToolChainArchiveFile())
    }

    private File getToolChainArchiveFile() {
        return toolChainArchive.get().asFile
    }

    private static String md5Hash(File fileToHash) {
        FileInputStream fis = null
        try {
            fis = new FileInputStream(fileToHash)
            return DigestUtils.md5Hex(fis).toLowerCase(Locale.ENGLISH)
        } finally {
            IOUtils.closeQuietly(fis)
        }
    }
}
