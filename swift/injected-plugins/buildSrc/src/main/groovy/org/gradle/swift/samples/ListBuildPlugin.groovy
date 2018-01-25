package org.gradle.swift.samples

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

                // Generate a missing source file
                tasks.create("generateSources", GenerateSource) {
                    sourceFile = '''
// A linked list node
class Node {
    let data: String
    var next: Node?

    init(data: String) {
        self.data = data
    }
}
'''
                }

                // Copy the existing sources and modify them
                tasks.create("modifySources", Sync) {
                    from "src/main/swift"
                    from tasks.generateSources
                    into layout.buildDirectory.dir("generated/main/swift")

                    exclude "**/DoNotCompile.swift"
                    filter(org.apache.tools.ant.filters.ReplaceTokens, tokens: [ REPLACEME: "Hello, from Gradle build" ])
                }

                library.source {
                    from = [ tasks.modifySources ]
                }
            }
        }
    }
}
