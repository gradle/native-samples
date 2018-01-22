package org.gradle.samples

import org.gradle.samples.fixtures.Samples
import org.gradle.testkit.runner.GradleRunner
import spock.lang.Specification
import spock.lang.Unroll

class WellBehavedSampleIntegrationTest extends Specification {
    @Unroll
    def "can run help for '#sample.name' without running any setup steps"() {
        given:
        sample.clean()

        expect:
        GradleRunner.create()
            .withProjectDir(sample.sampleDir)
            .withArguments("help")
            .build()

        where:
        sample << Samples.getAllSamples()
    }
}