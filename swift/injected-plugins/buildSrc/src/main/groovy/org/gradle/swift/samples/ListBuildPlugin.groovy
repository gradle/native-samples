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
                def generateSources = tasks.register("generateSources", GenerateSource) {
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
                def modifySources = tasks.register("modifySources", Sync) {
                    from "src/main/swift"
                    from generateSources
                    into layout.buildDirectory.dir("generated/main/swift")

                    def replaceableTokens = [ REPLACEME: "Hello, from Gradle build" ]
                    inputs.properties replaceableTokens

                    exclude "**/DoNotCompile.swift"
                    filter(org.apache.tools.ant.filters.ReplaceTokens, tokens: replaceableTokens)
                }

                library.source {
                    from = [ modifySources ]
                }
            }
        }
    }
}
