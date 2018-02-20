package org.gradle.samples

import org.gradle.internal.os.OperatingSystem
import org.gradle.samples.fixtures.NativeSample
import org.gradle.testkit.runner.GradleRunner
import spock.lang.Specification

abstract class ExecuteSamplesIntegrationTest extends Specification {

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

    boolean cmakeAvailable() {
        !OperatingSystem.current().isWindows() && OperatingSystem.current().findInPath("cmake") != null
    }
}
