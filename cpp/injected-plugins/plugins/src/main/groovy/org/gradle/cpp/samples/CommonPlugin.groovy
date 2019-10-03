package org.gradle.cpp.samples

import org.gradle.api.*

class CommonPlugin implements Plugin<Project> {
    void apply(Project project) {
        project.allprojects {
            apply plugin: 'cpp-library'
            apply plugin: 'xcode'
            apply plugin: 'visual-studio'

            group = 'org.gradle.cpp-samples'
            version = '1.0-SNAPSHOT'
        }
    }
}
