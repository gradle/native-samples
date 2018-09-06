package org.gradle.samples.plugins.generators


open class GitRepoTask : UpdateRepoTask() {
    override val deleteRepo: Boolean
        get() {
            return true
        }
}