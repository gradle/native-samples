package org.gradle.swift.samples

import org.gradle.api.*

class CommonPlugin implements Plugin<Project> {
    void apply(Project project) {
        project.allprojects {
            apply plugin: 'swift-library'
            apply plugin: 'xcode'
            apply plugin: 'xctest'

            group = 'org.gradle.swift-samples'
            version = '1.0-SNAPSHOT'
        }
    }
}
