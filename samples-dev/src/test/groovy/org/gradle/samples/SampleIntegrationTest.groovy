package org.gradle.samples

import org.gradle.internal.os.OperatingSystem
import org.gradle.samples.fixtures.Samples
import org.gradle.testkit.runner.GradleRunner
import org.junit.Assume
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification
import spock.lang.Unroll

class SampleIntegrationTest extends Specification {
    @Rule TemporaryFolder tmpDir = new TemporaryFolder()

    @Unroll
    def "can run '#target.name'"() {
        if (target.name.startsWith('swift')) {
            Assume.assumeTrue(null != findInPath('swiftc'))
        }

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
        target << Samples.getSamples()
    }

    private static File findInPath(String name) {
        return OperatingSystem.current().findInPath(name)
    }
}