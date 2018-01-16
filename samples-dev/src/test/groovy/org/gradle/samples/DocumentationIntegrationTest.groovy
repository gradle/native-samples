package org.gradle.samples

import org.gradle.samples.fixtures.Documentation
import org.gradle.samples.fixtures.Samples
import spock.lang.Specification
import spock.lang.Unroll

class DocumentationIntegrationTest extends Specification {
    @Unroll
    def "sample has documentation '#target.name'"() {
        expect:
        def sample = Documentation.getSample(target.sampleName)
        sample != null
        sample.hasInstructions()

        where:
        target << Samples.getSamples()
    }
}
