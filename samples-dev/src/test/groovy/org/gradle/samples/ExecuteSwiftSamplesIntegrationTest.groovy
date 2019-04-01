package org.gradle.samples

import org.gradle.internal.os.OperatingSystem
import org.gradle.samples.fixtures.Samples
import org.gradle.samples.fixtures.SwiftPmRunner
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Assume
import spock.lang.Requires
import spock.lang.Unroll

@Requires({ !OperatingSystem.current().windows })
class ExecuteSwiftSamplesIntegrationTest extends ExecuteSamplesIntegrationTest {

    @Unroll
    def "can build Swift '#sample.name'"() {
        // TODO - remove this once documentation parsing can better understand the setup
        Assume.assumeTrue(sample.sampleName != 'swift-package-manager-publish')

        // Tool chains can only be provision on Linux for Swift
        Assume.assumeFalse(sample.sampleName == 'provisionable-tool-chains' && !OperatingSystem.current().linux)

        // iOS application can only build on macOS
        Assume.assumeFalse(sample.sampleName == 'ios-application' && !OperatingSystem.current().macOsX)

        // TODO - Support tool chain selection to support Swift 3, 4 and 5
        Assume.assumeTrue(sample.sampleName != 'swift-versions')

        // TODO - extract this from the documentation
        boolean testsBroken = sample.sampleName == 'source-dependencies' || sample.sampleName == 'dependency-on-upstream-branch'

        given:
        sample.clean()
        runSetupFor(sample)

        expect:
        def runner = GradleRunner.create()
                .withProjectDir(sample.workingDir)
                .withArguments("build")
        if (testsBroken) {
            def result = runner.buildAndFail()
            assert result.taskPaths(TaskOutcome.FAILED) == [":xcTest"] : "Expected tests to fail:\n${result.output}"
        } else {
            runner.build()
        }

        GradleRunner.create()
                .withProjectDir(sample.workingDir)
                .withArguments("xcode")
                .build()

        GradleRunner.create()
                .withProjectDir(sample.workingDir)
                .withArguments("assembleRelease")
                .build()

        where:
        sample << Samples.getSamples("swift")
    }

    @Unroll
    def "can build 'swift-package-manager-publish'"() {
        given:
        def sample = Samples.useSampleIn("swift/swift-package-manager-publish")
        sample.clean()

        expect:
        GradleRunner.create()
                .withProjectDir(sample.sampleDir.parentFile.parentFile)
                .withArguments("generateRepos")
                .build()

        GradleRunner.create()
                .withProjectDir(new File(sample.sampleDir, "list-library"))
                .withArguments("build", "release")
                .build()

        SwiftPmRunner.create()
                .withProjectDir(new File(sample.sampleDir, "list-library"))
                .withArguments("build")
                .build()

        GradleRunner.create()
                .withProjectDir(new File(sample.sampleDir, "utilities-library"))
                .withArguments("build", "release")
                .build()

        SwiftPmRunner.create()
                .withProjectDir(new File(sample.sampleDir, "utilities-library"))
                .withArguments("build")
                .build()

        SwiftPmRunner.create()
                .withProjectDir(new File(sample.sampleDir, "app"))
                .withArguments("build")
                .build()
    }

    String getSampleLanguage() { 'swift' }
}
