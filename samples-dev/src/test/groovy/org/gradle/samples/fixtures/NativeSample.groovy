package org.gradle.samples.fixtures

import org.gradle.samples.fixtures.Documentation.SampleDocumentation

class NativeSample {
    final String name
    final String languageName
    final String sampleName
    final File rootSampleDir
    final SampleDocumentation documentation

    NativeSample(String name, String languageName, String sampleName, File rootSampleDir, SampleDocumentation documentation) {
        this.name = name
        this.languageName = languageName
        this.sampleName = sampleName
        this.rootSampleDir = rootSampleDir
        this.documentation = documentation
    }

    File getSampleDir() {
        return new File(rootSampleDir, name)
    }

    File getWorkingDir() {
        return new File(rootSampleDir, documentation.workingDir)
    }

    void clean() {
        def projectDir = sampleDir

        def toDelete = []
        projectDir.eachDirRecurse { d ->
            if (d.name == 'build' || d.name == '.gradle' || d.name.endsWith('.xcworkspace') || d.name.endsWith('.xcodeproj') || d.name == 'repos') {
                toDelete.add(d)
            }
        }
        toDelete.each { d -> d.deleteDir()
        }
    }
}
