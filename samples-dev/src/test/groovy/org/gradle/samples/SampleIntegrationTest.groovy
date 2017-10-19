package org.gradle.samples

import org.gradle.internal.os.OperatingSystem
import org.gradle.samples.fixtures.NativeSample
import org.gradle.testkit.runner.GradleRunner
import org.junit.Assume
import spock.lang.Specification
import spock.lang.Unroll

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS
import static org.gradle.testkit.runner.TaskOutcome.UP_TO_DATE

class SampleIntegrationTest extends Specification {
    def setup() {
        Assume.assumeNotNull(findInPath('swiftc'))
    }

    @Unroll
    def "can run '#target.name'"() {
        Assume.assumeFalse("The sample has been ignored", target.ignored)

        when:
        def result = GradleRunner.create()
                .withProjectDir(target.sampleDir)
                .withArguments(target.tasks)
                .build()

        then:
        def linkReleaseTasks = result.tasks.findAll { it.path.endsWith('linkRelease') }
        linkReleaseTasks*.outcome.every { it == SUCCESS || it == UP_TO_DATE }

        where:
        target << [
            sample('cpp/composite-build'),
            sample('cpp/executable'),
            sample('cpp/simple-library'),
            sample('cpp/swift-package-manager'),
            sample('cpp/transitive-dependencies'),
            sample('cpp/source-dependencies', true),

            sample('swift/composite-build'),
            sample('swift/executable'),
            sample('swift/simple-library'),
            sample('swift/swift-package-manager'),
            sample('swift/transitive-dependencies'),
            sample('swift/source-dependencies', true),
        ]

    }

    @Unroll
    def "can run '#target.name' that depends on 'simple-library'"() {
        Assume.assumeFalse("The sample has been ignored", target.ignored)

        given:
        def simpleLibrary = sample("${target.language}/simple-library")
        GradleRunner.create()
                .withProjectDir(simpleLibrary.sampleDir)
                .withArguments(simpleLibrary.tasks)
                .build()

        when:
        def result = GradleRunner.create()
                .withProjectDir(target.sampleDir)
                .withArguments(target.tasks)
                .build()

        then:
        def linkReleaseTasks = result.tasks.findAll { it.path.endsWith('linkRelease') }
        linkReleaseTasks*.outcome.every { it == SUCCESS || it == UP_TO_DATE }

        where:
        target << [sample('cpp/prebuilt-binaries'), sample('cpp/binary-dependencies'), sample('swift/prebuilt-binaries')]
    }

    private NativeSample sample(String name, boolean ignored = false) {
        File sampleDir = getRootSampleDir()
        return new NativeSample(name: name, sampleDir: new File(sampleDir, name), ignored: ignored)
    }

    private File getRootSampleDir() {
        File result = new File(this.class.getProtectionDomain().getCodeSource().getLocation().getPath())
        while (!new File(result, 'settings.gradle').exists()) {
            result = result.parentFile
        }
        return result
    }

    private static File findInPath(String name) {
        return OperatingSystem.current().findInPath(name)
    }
}