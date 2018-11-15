package org.gradle.samples

import org.gradle.internal.os.OperatingSystem
import org.gradle.samples.fixtures.Samples
import org.gradle.samples.fixtures.SwiftPmRunner
import org.gradle.testkit.runner.GradleRunner
import org.junit.Assume
import spock.lang.Requires
import spock.lang.Unroll

class ExecuteCppSamplesIntegrationTest extends ExecuteSamplesIntegrationTest {
    @Unroll
    def "can build C++ '#sample.name'"() {
        // TODO - remove these once documentation parsing can better understand the setup
        Assume.assumeTrue(sample.sampleName != 'swift-package-manager-publish')

        // CMake may not be available
        if (sample.name.contains('cmake') || sample.name == "cpp/library-with-tests") {
            Assume.assumeTrue(cmakeAvailable())
        }

        if (sample.name.contains('autotools')) {
            Assume.assumeTrue(notWindows())
        }

        if (sample.name == "cpp/windows-resources") {
            Assume.assumeTrue(isWindows())
        }

        // Tool chains can only be provision on Linux and macOS for C++
        Assume.assumeFalse(sample.sampleName == 'provisionable-tool-chains' && OperatingSystem.current().windows)

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
        sample << Samples.getSamples("cpp")
    }

    @Requires({ !OperatingSystem.current().windows })
    def "can build 'swift-package-manager-publish'"() {
        given:
        def sample = Samples.useSampleIn("cpp/swift-package-manager-publish")
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

    String getSampleLanguage() { 'cpp' }
}
