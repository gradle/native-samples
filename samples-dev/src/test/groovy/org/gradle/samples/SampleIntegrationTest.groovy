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

class SampleIntegrationTest extends Specification {
    @Rule TemporaryFolder tmpDir = new TemporaryFolder();

    @Unroll
    def "can run '#target.name'"() {
        if (target.name.startsWith('swift')) {
            Assume.assumeNotNull(findInPath('swiftc'))
        }
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

        expect:
        GradleRunner.create()
            .withProjectDir(target.sampleDir)
            .withArguments(target.tasks)
            .build()

        where:
        target << getSamples()
    }

    private List<NativeSample> getSamples() {
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