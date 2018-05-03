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
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.OutputFiles
import org.gradle.api.tasks.TaskAction
import org.gradle.workers.IsolationMode
import org.gradle.workers.WorkerConfiguration
import org.gradle.workers.WorkerExecutor

import javax.annotation.Nullable
import javax.inject.Inject

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

    private final WorkerExecutor workerExecutor

    @Inject
    DownloadToolChainTask(WorkerExecutor workerExecutor) {
        this.workerExecutor = workerExecutor
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
        workerExecutor.submit(Download) { WorkerConfiguration config ->
            config.isolationMode = IsolationMode.NONE
            config.params(SerializableOptional.ofNullable(fromUrl.getOrNull()), SerializableOptional.ofNullable(md5.getOrNull()), doneFile, temporaryDir, toolChainArchive.get().asFile)
        }
    }

    static class Download implements Runnable {
        private final SerializableOptional<URL> fromUrl
        private final File doneFile
        private final File temporaryDir
        private final SerializableOptional<String> md5
        private final File toolChainArchiveFile

        @Inject
        Download(SerializableOptional<URL> fromUrl, SerializableOptional<String> md5, File doneFile, File temporaryDir, File toolChainArchiveFile) {
            this.toolChainArchiveFile = toolChainArchiveFile
            this.md5 = md5
            this.temporaryDir = temporaryDir
            this.doneFile = doneFile
            this.fromUrl = fromUrl
        }

        @Override
        void run() {
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
            return toolChainArchiveFile.exists() && doneFile.exists() && doneFile.text == md5Hash(toolChainArchiveFile)
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

    // See https://github.com/gradle/gradle/issues/2405
    static class SerializableOptional<T> implements Serializable {
        final T value

        SerializableOptional(T value) {
            this.value = value
        }

        static SerializableOptional<T> ofNullable(@Nullable T obj) {
            return new SerializableOptional<T>(obj)
        }

        boolean isPresent() {
            return value != null
        }

        T get() {
            return value
        }
    }
}
