package org.gradle.cpp.samples

import org.gradle.api.initialization.*
import org.gradle.api.*
import org.gradle.api.tasks.Sync

class ListBuildPlugin implements Plugin<Settings> {
    void apply(Settings settings) {
        settings.with {
            rootProject.name = 'list'
            gradle.rootProject {
                // share common configuration across several builds
                apply plugin: CommonPlugin
            }
        }
    }
}
