package org.gradle.samples.plugins.generators

import org.eclipse.jgit.api.Git
import java.io.File


class Changes(val workDir: File, val git: Git) {
    var tag: String? = null
    var branch: String? = null
    var message: String? = null

    fun file(name: String): File {
        return File(workDir, name)
    }

    fun changeContent(srcFileName: String, pattern: String, replacement: String) {
        val srcFile = File(workDir, srcFileName)
        val original = srcFile.readText()
        if (!original.contains(pattern)) {
            throw IllegalArgumentException("Source file ${srcFile} does not contain anything that matches '${pattern}'.")
        }
        srcFile.writeText(original.replace(pattern, replacement))
    }

    fun tag(tag: String) {
        this.tag = tag
    }

    fun branch(branch: String) {
        this.branch = branch
    }

    fun message(message: String) {
        this.message = message
    }

    fun checkout(branch: String) {
        git.checkout().setName(branch).call()
    }
}