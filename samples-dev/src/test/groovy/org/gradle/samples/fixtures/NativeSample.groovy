package org.gradle.samples.fixtures

import org.apache.tools.ant.DirectoryScanner

class NativeSample {
    String name
    String sampleName
    File rootSampleDir

    File getSampleDir() {
        return new File(rootSampleDir, name)
    }

    NativeSample copyToTemp(File destination) {
        def projectDir = new File(destination, name)

        try {
            DirectoryScanner.removeDefaultExclude("**/.git")
            DirectoryScanner.removeDefaultExclude("**/.git/**")
            new AntBuilder().copy(todir: projectDir) {
                fileset(dir: sampleDir) {
                    exclude(name: "**/build/**")
                    exclude(name: "**/.gradle/**")
                    exclude(name: "**/.xcworkspace/**")
                    exclude(name: "**/.xcodeproj/**")
                }
            }
        } finally {
            DirectoryScanner.resetDefaultExcludes()
        }

        assert !new File(projectDir, '/build').exists()
        assert !new File(projectDir, '/.gradle').exists()
        assert new File(projectDir, "build.gradle").exists()
        assert new File(projectDir, "settings.gradle").exists()

        return new NativeSample(name: name, rootSampleDir: destination)
    }
}
