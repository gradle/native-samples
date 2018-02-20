package org.gradle.samples

import org.gradle.samples.fixtures.Samples
import org.gradle.testkit.runner.GradleRunner
import spock.lang.Specification
import spock.lang.Unroll

class BuildScanIntegrationTest extends Specification {
    @Unroll
    def "can generate build scan for '#sample.name'"() {
        given:
        expect:
        GradleRunner.create()
                .withProjectDir(sample.workingDir)
                .withArguments("tasks",
                "-I", "../../build-scan/buildScanAccept.gradle",
                "-I", "../../build-scan/buildScanUserData.gradle",
                "--scan")
                .forwardOutput()
                .build()

        where:
        sample << Samples.getAllSamples()
    }
}
