package org.gradle.samples

import org.gradle.samples.fixtures.Samples
import org.gradle.testkit.runner.GradleRunner
import spock.lang.Unroll

class ExecuteCSamplesIntegrationTest extends ExecuteSamplesIntegrationTest {
    @Unroll
    def "can build C '#sample.name'"() {
        given:
        sample.clean()
        runSetupFor(sample)

        expect:
        GradleRunner.create()
                .withProjectDir(sample.workingDir)
                .withArguments("build")
                .build()

        GradleRunner.create()
                .withProjectDir(sample.workingDir)
                .withArguments("xcode")
                .build()

        GradleRunner.create()
                .withProjectDir(sample.workingDir)
                .withArguments("assembleRelease")
                .build()

        where:
        sample << Samples.getSamples("c")
    }
    String getSampleLanguage() { 'c' }
}
