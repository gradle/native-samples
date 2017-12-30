package org.gradle.samples

import org.gradle.samples.fixtures.Documentation
import org.gradle.samples.fixtures.Samples
import spock.lang.Specification
import spock.lang.Unroll

class DocumentationIntegrationTest extends Specification {
    @Unroll
    def "sample has documentation '#target.name'"() {
        expect:
        Documentation.headings.find { it.endsWith("($target.sampleName)")}

        where:
        target << Samples.getSamples()
    }
}
