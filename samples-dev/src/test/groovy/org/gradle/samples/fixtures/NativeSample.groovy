package org.gradle.samples.fixtures

class NativeSample {
    String name
    String sampleName
    String languageName
    File rootSampleDir

    File getSampleDir() {
        return new File(rootSampleDir, name)
    }

    NativeSample copyToTemp(File destination) {
        def projectDir = new File(destination, name)
        new AntBuilder().copy(todir: projectDir) {
            fileset(dir: sampleDir) {
                exclude(name: "**/build/**")
                exclude(name: "**/.gradle/**")
                exclude(name: "**/.xcworkspace/**")
                exclude(name: "**/.xcodeproj/**")
            }
        }

        assert !new File(projectDir, '/build').exists()
        assert !new File(projectDir, '/.gradle').exists()
        assert new File(projectDir, "build.gradle").exists()
        assert new File(projectDir, "settings.gradle").exists()

        return new NativeSample(name: name, rootSampleDir: destination)
    }
}
