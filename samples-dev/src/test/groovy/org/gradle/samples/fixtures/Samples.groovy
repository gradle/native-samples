package org.gradle.samples.fixtures

import groovy.io.FileType

class Samples {
    static List<NativeSample> getAllSamples() {
        getSamples("cpp", "swift")
    }

    static List<NativeSample> getSamples(String... languages) {
        def result = []
        languages.collect { new File(rootSampleDir, it) }*.eachFile(FileType.DIRECTORIES) {
            if (it.name == 'repo') {
                return
            }

            def languageName = it.parentFile.name
            def sampleName = it.name
            result << new NativeSample(name: "$languageName/$sampleName", sampleName: sampleName, rootSampleDir: getRootSampleDir())
        }
        return result
    }

    static NativeSample useSampleIn(String sample) {
        def sampleDir = new File(rootSampleDir, sample)
        assert sampleDir.exists()
        def sourceSample = new NativeSample(name: sample, sampleName: sampleDir.getName(), rootSampleDir: getRootSampleDir())
        sourceSample.clean()
        return sourceSample
    }

    static File getRootSampleDir() {
        File result = new File(Samples.getProtectionDomain().getCodeSource().getLocation().getPath())
        while (!new File(result, 'settings.gradle').exists()) {
            result = result.parentFile
        }
        return result
    }
}
