package org.gradle.samples

import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.DependencyConstraint
import org.gradle.api.artifacts.ModuleDependency
import org.gradle.api.artifacts.PublishArtifact
import org.gradle.api.attributes.AttributeContainer
import org.gradle.api.attributes.Usage
import org.gradle.api.capabilities.Capability
import org.gradle.api.internal.component.UsageContext

class WrappedUsageContext implements UsageContext {
    private final String name
    private final Usage usage
    private final Configuration configuration

    WrappedUsageContext(String name, Usage usage, Configuration configuration) {
        this.configuration = configuration
        this.usage = usage
        this.name = name
    }

    @Override
    Usage getUsage() {
        return usage
    }

    @Override
    Set<? extends PublishArtifact> getArtifacts() {
        return configuration.artifacts
    }

    @Override
    Set<? extends ModuleDependency> getDependencies() {
        return configuration.dependencies
    }

    @Override
    Set<? extends DependencyConstraint> getDependencyConstraints() {
        return configuration.dependencyConstraints
    }

    @Override
    Set<? extends Capability> getCapabilities() {
        return Collections.emptySet()
    }

    @Override
    String getName() {
        return name
    }

    @Override
    AttributeContainer getAttributes() {
        return configuration.attributes
    }
}
