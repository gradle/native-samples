import org.gradle.internal.os.OperatingSystem
import org.gradle.testkit.runner.GradleRunner
import org.junit.Assume
import spock.lang.Specification
import spock.lang.Unroll

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS
import static org.gradle.testkit.runner.TaskOutcome.UP_TO_DATE

class SampleIntegrationTest extends Specification {
    @Unroll
    def "can use sample '#sampleName'"() {
        Assume.assumeNotNull(findInPath('swiftc'))

        when:
        def tasks = ['clean', 'assemble', 'linkRelease']
        if (usesMavenPublish(sampleDir)) {
            tasks << 'publish'
        }
        if (usesXcode(sampleDir)) {
            tasks << 'cleanXcode' << 'xcode'
        }
        def result = GradleRunner.create()
                .withProjectDir(sampleDir)
                .withArguments(tasks)
                .build()

        then:
        def linkReleaseTasks = result.tasks.findAll { it.path.endsWith('linkRelease') }
        linkReleaseTasks*.outcome.every { it == SUCCESS || it == UP_TO_DATE }

        where:
        [sampleName, sampleDir] << getSamples()

    }

    private List<File> getSamples() {
        File sampleDir = getRootSampleDir()
        def result = [
//                'cpp/prebuild-binaries',
//                'cpp/binary-dependencies',
                'cpp/composite-build',
                'cpp/executable',
                'cpp/simple-library',
                'cpp/swift-package-manager',
                'cpp/transitive-dependencies',
//                'cpp/google-test',
//                'cpp/source-dependencies',

//                'swift/prebuilt-binaries',
//                'swift/binary-dependencies',
                'swift/composite-build',
                'swift/executable',
                'swift/simple-library',
                'swift/swift-package-manager',
                'swift/transitive-dependencies',
//                'swift/source-dependencies',
        ].collect { [it, new File(sampleDir, it)] }
        return result
    }

    private File getRootSampleDir() {
        File result = new File(this.class.getProtectionDomain().getCodeSource().getLocation().getPath())
        while (!new File(result, 'settings.gradle').exists()) {
            result = result.parentFile
        }
        return result
    }

    private static boolean usesXcode(File sampleDir) {
        return new File(sampleDir, "build.gradle").text.contains('xcode')
    }

    private static boolean usesMavenPublish(File sampleDir) {
        return new File(sampleDir, "build.gradle").text.contains('maven-publish')
    }

    private static File findInPath(String name) {
        return OperatingSystem.current().findInPath(name)
    }
}