package org.gradle.samples.fixtures

import groovy.io.FileType

class Samples {
    static List<NativeSample> getSamples() {
        def result = []
        ['swift', 'cpp'].collect { new File(rootSampleDir, it) }*.eachFile(FileType.DIRECTORIES) {
            if (it.name == 'repo') {
                return
            }

            def languageName = it.parentFile.name
            def sampleName = it.name
            result << new NativeSample(name: "$languageName/$sampleName", sampleName: sampleName, languageName: languageName, rootSampleDir: getRootSampleDir())
        }
        return result
    }

    static File getRootSampleDir() {
        File result = new File(Samples.getProtectionDomain().getCodeSource().getLocation().getPath())
        while (!new File(result, 'settings.gradle').exists()) {
            result = result.parentFile
        }
        return result
    }
}
