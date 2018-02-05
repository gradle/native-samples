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
    def "sample has documentation '#sample.name'"() {
        expect:
        def docs = documentation.getSample(sample)
        docs != null

        println "work dir: " + docs.workingDir
        println "instructions: " + docs.instructions
        println "setup: " + docs.setupSteps

        !docs.instructions.empty
        docs.workingDir.startsWith(sample.name)

        where:
        sample << Samples.getAllSamples()
    }
}
