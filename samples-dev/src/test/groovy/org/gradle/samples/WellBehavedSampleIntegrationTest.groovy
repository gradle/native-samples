package org.gradle.samples

import org.gradle.internal.os.OperatingSystem
import org.gradle.samples.fixtures.Samples
import org.gradle.testkit.runner.GradleRunner
import org.junit.Assume
import spock.lang.Specification
import spock.lang.Unroll

class WellBehavedSampleIntegrationTest extends Specification {
    @Unroll
    def "can run help for '#sample.name' without running any setup steps"() {
        // TODO - remove this when instruction parsing is smarter
        Assume.assumeTrue(sample.sampleName != 'swift-package-manager-publish')
        Assume.assumeTrue(sample.sampleName != 'cmake-library')
        Assume.assumeTrue(sample.sampleName != 'cmake-source-dependencies')
        // Tool chains can only be provision on Linux for Swift and Linux and macOS for C++
        Assume.assumeFalse(sample.languageName == 'swift' && sample.sampleName == 'provisionable-tool-chains' && OperatingSystem.current().macOsX)
        Assume.assumeFalse(sample.sampleName == 'provisionable-tool-chains' && OperatingSystem.current().windows)

        given:
        sample.clean()

        expect:
        new File(sample.workingDir, "gradlew").file
        new File(sample.workingDir, "gradlew.bat").file
        new File(sample.workingDir, "settings.gradle").file

        GradleRunner.create()
            .withProjectDir(sample.workingDir)
            .withArguments("help")
            .build()

        where:
        sample << Samples.getAllSamples()
    }
}