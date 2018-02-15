package org.gradle.samples

import org.gradle.internal.os.OperatingSystem
import org.gradle.samples.fixtures.NativeSample
import org.gradle.samples.fixtures.Samples
import org.gradle.samples.fixtures.SwiftPmRunner
import org.gradle.testkit.runner.GradleRunner
import org.junit.Assume
import spock.lang.Requires
import spock.lang.Specification
import spock.lang.Unroll

class ExecuteSamplesIntegrationTest extends Specification {
    @Unroll
    def "can build C++ '#sample.name'"() {
        // TODO - remove these once documentation parsing can better understand the setup
        Assume.assumeTrue(sample.sampleName != 'swift-package-manager-publish')

        // CMake is currently only available on Linux CI machines
        Assume.assumeFalse(sample.sampleName == 'cmake-library' && !OperatingSystem.current().linux)
        Assume.assumeFalse(sample.sampleName == 'cmake-source-dependencies')

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

    @Unroll
    def "can build C '#sample.name'"() {
        // Sample does not yet work on Windows
        Assume.assumeFalse(sample.sampleName == 'application' && OperatingSystem.current().windows)

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

    @Requires({ !OperatingSystem.current().windows })
    @Unroll
    def "can build Swift '#sample.name'"() {
        // TODO - remove this once Swift 4 tools installed on Linux CI machines
        Assume.assumeTrue(!OperatingSystem.current().linux || sample.sampleName != 'swift-versions')
        // TODO - remove this once documentation parsing can better understand the setup
        Assume.assumeTrue(sample.sampleName != 'swift-package-manager-publish')

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

    @Requires({ !OperatingSystem.current().windows })
    @Unroll
    def "can build #language 'swift-package-manager-publish'"() {
        given:
        def sample = Samples.useSampleIn("${language}/swift-package-manager-publish")
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

        where:
        language << ["swift", "cpp"]
    }

    def runSetupFor(NativeSample sample) {
        def docs = sample.documentation
        docs.setupSteps.each { command ->
            println "Running setup step " + command + " in " + docs.workingDir
            GradleRunner.create()
            .withProjectDir(sample.workingDir)
            .withArguments(command.split().drop(1))
            .build()
        }
    }
}
