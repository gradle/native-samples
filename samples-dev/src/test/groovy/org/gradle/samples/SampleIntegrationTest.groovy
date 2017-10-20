package org.gradle.samples

import groovy.io.FileType
import org.gradle.internal.os.OperatingSystem
import org.gradle.samples.fixtures.NativeSample
import org.gradle.testkit.runner.GradleRunner
import org.junit.Assume
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification
import spock.lang.Unroll

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS
import static org.gradle.testkit.runner.TaskOutcome.UP_TO_DATE

class SampleIntegrationTest extends Specification {
    @Rule TemporaryFolder tmpDir = new TemporaryFolder();
    // Goals
    // * Better assertion (light weight)
    def setup() {
        Assume.assumeNotNull(findInPath('swiftc'))
    }

    @Unroll
    def "can run '#target.name'"() {
        Assume.assumeFalse("The sample has been ignored", target.ignored)

        given:
        target = target.copyToTemp(tmpDir.root)
        target.dependencies.every {
            def result = GradleRunner.create()
                    .withProjectDir(it.sampleDir)
                    .withArguments(it.tasks)
                    .build()
            println result.output
        }

        when:
        def result = GradleRunner.create()
                .withProjectDir(target.sampleDir)
                .withArguments(target.tasks)
                .build()

        then:
        println result.output
        def linkReleaseTasks = result.tasks.findAll { it.path.endsWith('linkRelease') }
        linkReleaseTasks*.outcome.every { it == SUCCESS }

        where:
        target << getSamples()
    }

    List<NativeSample> getSamples() {
        def result = []
        ['swift', 'cpp'].collect { new File(rootSampleDir, it) }*.eachFile(FileType.DIRECTORIES) {
            if (it.name == 'repo') {
                return
            }

            def languageName = it.parentFile.name
            def sampleName = it.name
            result << sample("$languageName/$sampleName")
        }
        return result
    }

    private NativeSample sample(String name) {
        return new NativeSample(name: name, rootSampleDir: getRootSampleDir())
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