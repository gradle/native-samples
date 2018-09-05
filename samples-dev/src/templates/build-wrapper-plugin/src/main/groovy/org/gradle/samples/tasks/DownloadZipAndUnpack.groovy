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
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.tasks.Input
import org.gradle.api.provider.Property
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

import javax.inject.Inject

class DownloadZipAndUnpack extends DefaultTask {
    @Input Property<String> url
    @OutputDirectory final DirectoryProperty outputDirectory

    @Inject
    DownloadZipAndUnpack(ObjectFactory objectFactory) {
        url = objectFactory.property(String)
        outputDirectory = objectFactory.directoryProperty()
        outputDirectory.set(project.layout.buildDirectory.dir(project.name))
        onlyIf { url.isPresent() }
    }

    @TaskAction
    void doDownloadZipAndUnpack() {
        def downloadUrl = new URL(url.get())
        logger.warn("Downloading $downloadUrl")
        def zipDestination = new File(temporaryDir, "zip.zip")
        downloadUrl.withInputStream { zipBytes ->
            zipDestination.withOutputStream { it << zipBytes }
        }
        logger.warn("Downloaded to ${zipDestination.absolutePath}")

        def unzipDestination = outputDirectory.get().asFile
        project.copy {
            from project.zipTree(zipDestination)
            into unzipDestination
        }
    }
}
