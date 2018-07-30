package org.gradle.samples.fixtures

import groovy.io.FileType

class Samples {
    private static final Documentation documentation = new Documentation()

    static List<NativeSample> getAllSamples() {
        getSamples("cpp", "swift", "c")
    }

    static List<NativeSample> getSamples(String... languages) {
        def result = []
        languages.collect { new File(rootSampleDir, it) }*.eachFile(FileType.DIRECTORIES) {
            if (it.name == 'repo') {
                return
            }

            def languageName = it.parentFile.name
            def sampleName = it.name
            def name = "$languageName/$sampleName"
            result << new NativeSample(name, languageName, sampleName, rootSampleDir, documentation.getSample(name))
        }
        return result
    }

    static NativeSample useSampleIn(String sample) {
        def sampleDir = new File(rootSampleDir, sample)
        assert sampleDir.exists()
        def sourceSample = new NativeSample(sample, sampleDir.getParentFile().getName(), sampleDir.getName(), rootSampleDir, documentation.getSample(sample))
        return sourceSample
    }

    static File getRootSampleDir() {
        File result = new File(Samples.getProtectionDomain().getCodeSource().getLocation().getPath())
        while (!new File(result, 'settings.gradle.kts').exists()) {
            result = result.parentFile
        }
        return result
    }
}
