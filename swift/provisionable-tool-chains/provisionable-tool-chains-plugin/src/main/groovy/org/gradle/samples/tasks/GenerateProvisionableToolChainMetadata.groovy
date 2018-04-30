package org.gradle.samples.tasks

import groovy.transform.CompileStatic
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.samples.ProvisionableToolChain

@CompileStatic
class GenerateProvisionableToolChainMetadata extends DefaultTask {
    @Nested
    final List<ProvisionableToolChain> toolChains = new ArrayList<ProvisionableToolChain>();

    final DirectoryProperty repositoryDirectory = newInputDirectory()

    @OutputFile
    final RegularFileProperty outputFile = newOutputFile()

    @TaskAction
    private void generate() {
        outputFile.get().asFile.text = """{
            "repositoryDirectory": "${repositoryDirectory.get().asFile.absolutePath}",
            "toolChains": [
                ${toolChains.collect({ generateProvisionedToolChain(it) }).join(",")}
            ]
        }
        """
    }

    String generateProvisionedToolChain(ProvisionableToolChain toolChain) {
        return """{
            "type": "${toolChain.type.get().canonicalName}",
            "name": "${toolChain.name}",
            "location": "${toolChain.location.get().asFile.absolutePath}",
            "url": "${toolChain.url.getOrNull()}",
            "md5": "${toolChain.md5.getOrNull()}",
            "cCompilerExecutable": "${toolChain.cCompilerExecutable.getOrNull()}",
            "cppCompilerExecutable": "${toolChain.cppCompilerExecutable.getOrNull()}",
            "linkerExecutable": "${toolChain.linkerExecutable.getOrNull()}",
            "staticLibArchiverExecutable": "${toolChain.staticLibArchiverExecutable.getOrNull()}",
        }
        """
    }

    @Input
    protected File getRepositoryDirectoryInput() {
        return repositoryDirectory.get().asFile
    }
}
