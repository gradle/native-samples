package org.gradle.samples.plugins.wrapper

import groovy.json.JsonSlurper
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.wrapper.Wrapper
import java.net.URL

/**
 * Adds a 'nightlyWrapper' task.
 */
class WrapperPlugin: Plugin<Project> {
    override fun apply(project: Project) {
        project.tasks.register("nightlyWrapper", Wrapper::class.java) {task ->
            println("Locating nightly version ...")
            val result = JsonSlurper().parseText(URL("https://services.gradle.org/versions/nightly").readText()) as Map<String, String>
            if (result.isEmpty()) {
                throw GradleException("Cannot update wrapper to 'nightly' version as there is currently no version of that label")
            }
            val version = result["version"]
            val url = result["downloadUrl"]
            task.apply {
                doFirst {
                    println("Updating wrapper to nightly version: $version (downloadUrl: $url)")
                }
                distributionUrl = url
                group = "wrapper"
                description = "Updates the samples to use the most recent nightly"
            }
        }
    }
}