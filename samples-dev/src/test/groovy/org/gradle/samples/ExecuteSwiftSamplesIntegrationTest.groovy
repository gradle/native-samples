package org.gradle.samples

import org.gradle.internal.os.OperatingSystem
import org.gradle.samples.fixtures.Samples
import org.gradle.samples.fixtures.SwiftPmRunner
import org.gradle.testkit.runner.GradleRunner
import org.junit.Assume
import spock.lang.Requires
import spock.lang.Unroll

@Requires({ !OperatingSystem.current().windows })
class ExecuteSwiftSamplesIntegrationTest extends ExecuteSamplesIntegrationTest {

    @Unroll
    def "can build Swift '#sample.name'"() {
        // TODO - remove this once Swift 4 tools installed on Linux CI machines
        Assume.assumeTrue(!OperatingSystem.current().linux || sample.sampleName != 'swift-versions')
        // TODO - remove this once documentation parsing can better understand the setup
        Assume.assumeTrue(sample.sampleName != 'swift-package-manager-publish')

        // Tool chains can only be provision on Linux for Swift
        Assume.assumeFalse(sample.sampleName == 'provisionable-tool-chains' && !OperatingSystem.current().linux)

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

    // TODO - replace this once Swift 4 tools installed on Linux CI machines
    @Requires({ OperatingSystem.current().linux })
    def "can build Swift 'swift-versions' with Swift 3 toolchain"() {
        given:
        def sample = Samples.useSampleIn("swift/swift-versions")
        sample.clean()
        runSetupFor(sample)

        expect:
        GradleRunner.create()
                .withProjectDir(sample.workingDir)
                .withArguments("swift3-app:build")
                .build()
    }
}
