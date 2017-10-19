package org.gradle.samples.fixtures

class NativeSample {
    String name
    File sampleDir
    boolean ignored

    String getLanguage() {
        name.split('/')[0]
    }

    List<String> getTasks() {
        def tasks = ['clean', 'assemble', 'linkRelease']
        if (usesMavenPublish) {
            tasks << 'publish'
        }
        if (usesXcode) {
            tasks << 'cleanXcode' << 'xcode'
        }
        return tasks
    }

    boolean isUsesXcode() {
        return new File(sampleDir, "build.gradle").text.contains('xcode')
    }

    boolean isUsesMavenPublish() {
        return new File(sampleDir, "build.gradle").text.contains('maven-publish')
    }
}
