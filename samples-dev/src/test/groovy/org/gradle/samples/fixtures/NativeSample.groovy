package org.gradle.samples.fixtures

class NativeSample {
    String name
    String sampleName
    File rootSampleDir

    File getSampleDir() {
        return new File(rootSampleDir, name)
    }

    void clean() {
        def projectDir = sampleDir

        def toDelete = []
        projectDir.eachDirRecurse { d ->
            if (d.name == 'build' || d.name == '.gradle' || d.name.endsWith('.xcworkspace') || d.name.endsWith('.xcodeproj')) {
                toDelete.add(d)
            }
        }
        toDelete.each { d ->
            d.deleteDir()
        }
    }
}
