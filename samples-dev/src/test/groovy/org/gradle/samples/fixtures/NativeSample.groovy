package org.gradle.samples.fixtures

class NativeSample {
    String name
    File rootSampleDir
    boolean ignored
    private List<String> tasks

    String getLanguage() {
        name.split('/')[0]
    }

    File getSampleDir() {
        return new File(rootSampleDir, name)
    }

    List<String> getTasks() {
        if (tasks) {
            return tasks
        }

        def tasks = ['clean', 'assemble', 'linkRelease']
        if (usesMavenPublish) {
            tasks << 'publish'
        }
        if (usesXcode) {
            tasks << 'cleanXcode' << 'xcode'
        }
        return tasks
    }

    List<NativeSample> getDependencies() {
        new File(sampleDir, 'settings.gradle').readLines().findAll { it.startsWith('// dependsOn') }.collect {
            def tokens = it.split(' ')
            def name = tokens[2]
            def tasks = tokens[3].split(',')

            return new NativeSample(name: name, tasks: tasks, rootSampleDir: rootSampleDir, ignored: ignored)
        }
    }

    boolean isUsesXcode() {
        return new File(sampleDir, "build.gradle").text.contains('xcode')
    }

    boolean isUsesMavenPublish() {
        return new File(sampleDir, "build.gradle").text.contains('maven-publish')
    }
}
