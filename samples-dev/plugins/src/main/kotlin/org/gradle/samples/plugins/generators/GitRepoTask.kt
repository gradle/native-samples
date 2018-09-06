package org.gradle.samples.plugins.generators

import org.eclipse.jgit.api.Git
import org.gradle.api.Action
import org.gradle.api.DefaultTask
import org.gradle.api.file.RelativePath
import org.gradle.api.tasks.TaskAction
import java.io.File


open class GitRepoTask : DefaultTask() {
    val sampleDir = project.objects.directoryProperty()
    val changes = ArrayList<Action<in Changes>>()

    fun change(cl: Action<in Changes>) {
        changes.add(cl)
    }

    @TaskAction
    fun createRepo() {
        val destDir = sampleDir.get().asFile
        val parentIgnoreFile = File(destDir.parentFile, ".gitignore")
        val parentIgnore = "${destDir.name}/\n"
        if (!parentIgnoreFile.isFile() || !parentIgnoreFile.readText().contains(parentIgnore)) {
            parentIgnoreFile.appendText(parentIgnore)
        }
        project.delete(File(destDir, ".git"))
        val init = Git.init()
        val git = init.setDirectory(destDir).call()
        try {
            File(destDir, ".gitignore").writeText("""
/.gradle
build
/.build
""")
            val files = ArrayList<RelativePath>()
            project.fileTree(destDir).visit { f ->
                if (f.file.isFile) {
                    files.add(f.relativePath)
                }
            }
            val add = git.add()
            add.addFilepattern(".gitignore")
            files.forEach {
                add.addFilepattern(it.pathString)
            }
            add.call()

            changes.forEach { change ->
                val changes = Changes(destDir, git)
                change.execute(changes)
                if (changes.branch != null) {
                    git.branchCreate().setName(changes.branch).call()
                    git.checkout().setName(changes.branch).call()
                }
                git.commit().setAll(true).setMessage(changes.message).call()
                if (changes.tag != null) {
                    git.tag().setName(changes.tag).call()
                }
            }
            if (changes.isEmpty()) {
                git.commit().setAll(true).setMessage("initial version").call()
            }
        } finally {
            git.close()
        }
    }
}
