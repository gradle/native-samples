package org.gradle.samples

import groovy.transform.CompileStatic
import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty

@CompileStatic
class ProvisionableToolChainExtension {
    final DirectoryProperty repositoryDirectory
    final NamedDomainObjectContainer<ProvisionableToolChain> toolChains

    ProvisionableToolChainExtension(Project project) {
        repositoryDirectory = project.layout.directoryProperty()
        toolChains = project.container(ProvisionableToolChain) { String name ->
            project.objects.newInstance(ProvisionableToolChain, name)
        }
    }

    void toolChains(Action<NamedDomainObjectContainer<? extends ProvisionableToolChain>> action) {
        action.execute(toolChains)
    }
}
