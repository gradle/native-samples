package org.gradle.samples.fixtures

class NativeSample {
    String name
    File sampleDir

    boolean isUsesXcode() {
        return new File(sampleDir, "build.gradle").text.contains('xcode')
    }

    boolean isUsesMavenPublish() {
        return new File(sampleDir, "build.gradle").text.contains('maven-publish')
    }
}
