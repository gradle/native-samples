package org.gradle.sample.plugins.generators

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.InitCommand
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.TaskAction

class GitRepoTask extends DefaultTask {
    final DirectoryProperty sampleDir = project.layout.directoryProperty()
    private final List<Closure> changes = []

    void change(Closure cl) {
        changes << cl
    }

    @TaskAction
    def go() {
        def destDir = sampleDir.get().asFile
        project.delete(new File(destDir, ".git"))
        InitCommand init = Git.init();
        def git = init.setDirectory(destDir).call()
        try {
            new File(destDir, ".gitignore").text = """
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

            changes.each { change ->
                def changes = new Changes(destDir, git)
                change.resolveStrategy = Closure.DELEGATE_FIRST
                change.delegate = changes
                def message = change(changes)
                if (changes.branch != null) {
                    git.branchCreate().setName(changes.branch).call()
                    git.checkout().setName(changes.branch).call()
                }
                git.commit().setAll(true).setMessage(message).call()
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

class Changes {
    private final File workDir
    private final Git git
    private String tag
    private String branch

    Changes(File workDir, Git git) {
        this.workDir = workDir
        this.git = git
    }

    File file(String name) {
        return new File(workDir, name)
    }

    void changeContent(String srcFileName, String pattern, String replacement) {
        def srcFile = new File(workDir, srcFileName)
        def original = srcFile.text
        if (!original.find(pattern)) {
            throw new IllegalArgumentException("Source file ${srcFile} does not contain anything that matches '${pattern}'.")
        }
        srcFile.text = original.replace(pattern, replacement)
    }

    void tag(String tag) {
        this.tag = tag
    }

    void branch(String branch) {
        this.branch = branch
    }

    void checkout(String branch) {
        git.checkout().setName(branch).call()
    }
}
