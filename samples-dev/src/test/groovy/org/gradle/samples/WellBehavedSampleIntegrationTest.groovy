package org.gradle.samples

import org.gradle.samples.fixtures.Samples
import org.gradle.testkit.runner.GradleRunner
import spock.lang.Specification
import spock.lang.Unroll

class WellBehavedSampleIntegrationTest extends Specification {
    @Unroll
    def "can run help for '#sample.name'"() {
        given:
        def target = Samples.useSampleIn(sample.name)

        expect:
        GradleRunner.create()
            .withProjectDir(target.sampleDir)
            .withArguments("help")
            .build()

        where:
        sample << Samples.getAllSamples()
    }
}