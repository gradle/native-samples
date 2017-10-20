package org.gradle.samples.fixtures

import org.gradle.internal.impldep.org.apache.commons.io.FileUtils

class NativeSample {
    String name
    File rootSampleDir
    private List<String> tasks

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

    NativeSample copyToTemp(File destination) {
        dependencies*.copyToTemp(destination)
        compositeDependencies*.copyToTemp(destination)

        FileUtils.copyDirectory(sampleDir, new File(destination, name), new FileFilter() {
            @Override
            boolean accept(File pathname) {
                return !(pathname.name in ['build', '.gradle'])
            }
        })

        return new NativeSample(name: name, rootSampleDir: destination, tasks: tasks)
    }

    boolean isIgnored() {
        return null != new File(sampleDir, 'settings.gradle').readLines().find { it.startsWith('// ignored') }
    }

    List<NativeSample> getDependencies() {
        new File(sampleDir, 'settings.gradle').readLines().findAll { it.startsWith('// dependsOn') }.collect {
            def tokens = it.split(' ')
            def name = tokens[2]
            def tasks = tokens[3].split(',')

            return new NativeSample(name: name, tasks: tasks, rootSampleDir: rootSampleDir)
        }
    }

    List<NativeSample> getCompositeDependencies() {
        new File(sampleDir, 'settings.gradle').readLines().findAll { it.startsWith('// copy') }.collect {
            def tokens = it.split(' ')
            def name = tokens[2]

            return new NativeSample(name: name, rootSampleDir: rootSampleDir)
        }
    }

    boolean isUsesXcode() {
        return new File(sampleDir, "build.gradle").text.contains('xcode')
    }

    boolean isUsesMavenPublish() {
        return new File(sampleDir, "build.gradle").text.contains('maven-publish')
    }
}
