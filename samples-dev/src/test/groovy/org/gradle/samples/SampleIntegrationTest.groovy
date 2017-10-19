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
    @Unroll
    def "can use sample '#sample.name'"() {
        Assume.assumeNotNull(findInPath('swiftc'))

        when:
        def tasks = ['clean', 'assemble', 'linkRelease']
        if (sample.usesMavenPublish) {
            tasks << 'publish'
        }
        if (sample.usesXcode) {
            tasks << 'cleanXcode' << 'xcode'
        }
        def result = GradleRunner.create()
                .withProjectDir(sample.sampleDir)
                .withArguments(tasks)
                .build()

        then:
        def linkReleaseTasks = result.tasks.findAll { it.path.endsWith('linkRelease') }
        linkReleaseTasks*.outcome.every { it == SUCCESS || it == UP_TO_DATE }

        where:
        sample << getSamples()

    }

    private List<NativeSample> getSamples() {
        File sampleDir = getRootSampleDir()
        def result = [
//                'cpp/prebuild-binaries',  // mustRunAfter(getTestTaskFor('cpp/simple-library'))
//                'cpp/binary-dependencies',  // mustRunAfter(getTestTaskFor('cpp/simple-library'))
                'cpp/composite-build',
                'cpp/executable',
                'cpp/simple-library',
                'cpp/swift-package-manager',
                'cpp/transitive-dependencies',
//                'cpp/google-test',  // enabled = false
//                'cpp/source-dependencies',  // enabled = false

//                'swift/prebuilt-binaries',  // mustRunAfter(getTestTaskFor('cpp/simple-library'))
                'swift/composite-build',
                'swift/executable',
                'swift/simple-library',
                'swift/swift-package-manager',
                'swift/transitive-dependencies',
//                'swift/source-dependencies',  // enabled = false
        ].collect { new NativeSample(name: it, sampleDir: new File(sampleDir, it)) }
        return result
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