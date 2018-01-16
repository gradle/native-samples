package org.gradle.samples

import org.gradle.samples.fixtures.Samples
import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification
import spock.lang.Unroll

class WellBehavedSampleIntegrationTest extends Specification {
    @Rule TemporaryFolder tmpDir = new TemporaryFolder()

    @Unroll
    def "can run help for '#sample.name'"() {
        given:
        def target = Samples.useSampleIn(sample.name, tmpDir.root)

        expect:
        GradleRunner.create()
            .withProjectDir(target.sampleDir)
            .withArguments("help")
            .build()

        where:
        sample << Samples.getAllSamples()
    }
}