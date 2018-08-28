package org.gradle.samples.internal;

import org.gradle.api.attributes.AttributeDisambiguationRule;
import org.gradle.api.attributes.MultipleCandidatesDetails;

import javax.inject.Inject;

public class SoftwareModelComponentSelectionDisambiguationRule implements AttributeDisambiguationRule<String> {
    private final String componentName;

    @Inject
    public SoftwareModelComponentSelectionDisambiguationRule(String componentName) {
        this.componentName = componentName;
    }

    @Override
    public void execute(MultipleCandidatesDetails<String> candidates) {
        candidates.closestMatch(componentName);
    }
}
