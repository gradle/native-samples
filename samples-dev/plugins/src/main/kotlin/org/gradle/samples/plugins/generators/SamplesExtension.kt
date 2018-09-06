package org.gradle.samples.plugins.generators

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.samples.plugins.SampleGeneratorTask
import javax.inject.Inject


open class SamplesExtension(project: Project) {
    val externalRepos = project.container(ExternalRepo::class.java) { name ->
        project.objects.newInstance(ExternalRepo::class.java, name, project.objects)
    }
}

open class ExternalRepo @Inject constructor(val name: String, objectFactory: ObjectFactory) {
    val repoUrl = objectFactory.property(String::class.java)
    val sourceActions = ArrayList<Action<in SampleGeneratorTask>>()

    fun copySource(action: Action<in SampleGeneratorTask>) {
        sourceActions.add(action)
    }
}