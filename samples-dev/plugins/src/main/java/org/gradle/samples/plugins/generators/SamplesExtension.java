package org.gradle.samples.plugins.generators;

import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.Project;

public class SamplesExtension {
    private final NamedDomainObjectContainer<ExternalRepo> externalRepos;

    public SamplesExtension(Project project) {
        externalRepos = project.container(ExternalRepo.class, name -> {
            return project.getObjects().newInstance(ExternalRepo.class, name);
        });
    }

    public NamedDomainObjectContainer<ExternalRepo> getExternalRepos() {
        return externalRepos;
    }
}
