package org.gradle.samples.plugins.generators;

import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.Project;

public class SamplesExtension {
    private final NamedDomainObjectContainer<ExternalRepo> externalRepos;
    private final NamedDomainObjectContainer<Sample> samples;

    public SamplesExtension(Project project) {
        externalRepos = project.container(ExternalRepo.class, name -> {
            return project.getObjects().newInstance(ExternalRepo.class, name);
        });
        samples = project.container(Sample.class, name -> {
            return project.getObjects().newInstance(Sample.class, name);
        });
    }

    public NamedDomainObjectContainer<ExternalRepo> getExternalRepos() {
        return externalRepos;
    }

    public NamedDomainObjectContainer<Sample> getSamples() {
        return samples;
    }
}
