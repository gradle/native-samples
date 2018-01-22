package org.gradle.samples

import org.gradle.internal.os.OperatingSystem
import org.gradle.samples.fixtures.Documentation
import org.gradle.samples.fixtures.NativeSample
import org.gradle.samples.fixtures.Samples
import org.gradle.testkit.runner.GradleRunner
import org.junit.Assume
import spock.lang.Requires
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

class ExecuteSamplesIntegrationTest extends Specification {
    @Shared
    Documentation documentation = new Documentation()

    @Unroll
    def "can build C++ '#sample.name'"() {
        given:
        def target = Samples.useSampleIn(sample.name)
        runSetupFor(target)

        expect:
        GradleRunner.create()
                .withProjectDir(target.sampleDir)
                .withArguments("build")
                .build()

        where:
        sample << Samples.getSamples("cpp")
    }

    @Requires({ !OperatingSystem.current().isWindows() })
    @Unroll
    def "can build Swift '#sample.name'"() {
        given:
        def target = Samples.useSampleIn(sample.name)
        runSetupFor(target)

        expect:
        GradleRunner.create()
                .withProjectDir(target.sampleDir)
                .withArguments("build")
                .build()

        where:
        sample << Samples.getSamples("swift")
    }

    def runSetupFor(NativeSample sample) {
        documentation.getSample(sample.sampleName).setupSteps.each { command ->
            println "Running setup step " + command
            GradleRunner.create()
            .withProjectDir(sample.sampleDir)
            .withArguments(command.split().drop(1))
            .build()
        }
    }
}
