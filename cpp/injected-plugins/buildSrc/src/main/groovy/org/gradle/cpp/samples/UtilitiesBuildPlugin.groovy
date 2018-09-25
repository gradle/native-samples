package org.gradle.cpp.samples

import org.gradle.api.initialization.*
import org.gradle.api.*

class UtilitiesBuildPlugin implements Plugin<Settings> {
    void apply(Settings settings) {
        settings.with {
            rootProject.name = 'utilities'
            gradle.rootProject {
                // share common configuration across several builds
                apply plugin: CommonPlugin

                // Supply build information for utilities build
                library {
                    dependencies {
                        api 'org.gradle.cpp-samples:list:1.+'
                    }
                }
            }
        }
    }
}
