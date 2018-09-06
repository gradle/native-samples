package org.gradle.samples.plugins.generators

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.MergeCommand
import org.gradle.api.DefaultTask
import org.gradle.api.model.ObjectFactory
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File
import javax.inject.Inject

/**
 * Clones/pulls changes from external repo.
 */
open class SyncExternalRepoTask @Inject constructor(objectFactory: ObjectFactory) : DefaultTask() {
    @Input
    val repoUrl = objectFactory.property(String::class.java)
    @OutputDirectory
    val checkoutDirectory = objectFactory.directoryProperty()

    init {
        outputs.upToDateWhen { false }
    }

    @TaskAction
    fun checkout() {
        val checkoutDir = checkoutDirectory.get().asFile
        if (File(checkoutDir, ".git").exists()) {
            println("Pull ${repoUrl.get()} into ${checkoutDir}")
            val git = Git.open(checkoutDir)
            try {
                git.pull().setFastForward(MergeCommand.FastForwardMode.FF_ONLY).call()
            } finally {
                git.close()
            }
        } else {
            println("Clone ${repoUrl.get()} into ${checkoutDir}")
            val git = Git.cloneRepository()
                    .setURI(repoUrl.get())
                    .setDirectory(checkoutDir)
                    .call()
            git.close()
        }
    }
}