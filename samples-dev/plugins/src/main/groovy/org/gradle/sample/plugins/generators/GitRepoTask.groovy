package org.gradle.sample.plugins.generators

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.InitCommand
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.TaskAction

class GitRepoTask extends DefaultTask {
    final DirectoryProperty sampleDir = project.layout.directoryProperty()

    Map<String, Closure> changes = [:]

    void changeContent(String srcFileName, String pattern, String replacement) {
        def srcFile = sampleDir.file(srcFileName).get().asFile
        def original = srcFile.text
        if (!original.find(pattern)) {
            throw new IllegalArgumentException("Source file ${srcFile} does not contain anything that matches '${pattern}'.")
        }
        srcFile.text = original.replace(pattern, replacement)
    }

    @TaskAction
    def go() {
        def destDir = sampleDir.get().asFile
        project.delete(new File(destDir, ".git"))
        InitCommand init = Git.init();
        Git git = init.setDirectory(destDir).call()
        try {
            new File(destDir, ".gitignore") << """
/.gradle
build
/.build
"""
            def files = []
            project.fileTree(destDir).visit { f ->
                if (f.file.file) {
                    files.add(f.relativePath)
                }
            }
            def add = git.add()
            add.addFilepattern(".gitignore")
            files.each {
                add.addFilepattern(it.pathString)
            }
            add.call()

            changes.each { tag, change ->
                def message = change(destDir)
                git.commit().setAll(true).setMessage(message).call()
                if (tag != 'SNAPSHOT') {
                    git.tag().setName(tag).call()
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
