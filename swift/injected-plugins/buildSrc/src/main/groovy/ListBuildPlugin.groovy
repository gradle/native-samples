import org.gradle.api.initialization.*
import org.gradle.api.*

class ListBuildPlugin implements Plugin<Settings> {
    void apply(Settings settings) {
        settings.rootProject.name = 'list'
        settings.gradle.allprojects {
            apply plugin: 'swift-library'
            apply plugin: 'xcode'
            apply plugin: 'xctest'

            group = 'org.gradle.swift-samples'
            version = '1.0-SNAPSHOT'
        }
    }
}
