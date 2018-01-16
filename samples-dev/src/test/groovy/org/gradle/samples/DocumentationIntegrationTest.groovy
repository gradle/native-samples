package org.gradle.samples

import org.gradle.samples.fixtures.Documentation
import org.gradle.samples.fixtures.Samples
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

class DocumentationIntegrationTest extends Specification {
    @Shared
    def documentation = new Documentation()

    @Unroll
    def "sample has documentation '#target.name'"() {
        expect:
        def sample = documentation.getSample(target.sampleName)
        sample != null
        sample.hasInstructions()

        where:
        target << Samples.getAllSamples()
    }
}
