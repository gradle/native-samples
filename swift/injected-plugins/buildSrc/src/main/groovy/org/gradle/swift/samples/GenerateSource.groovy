package org.gradle.swift.samples

import org.gradle.api.*
import org.gradle.api.tasks.*

class GenerateSource extends DefaultTask {
    @Input
    String sourceFile

    @OutputFile
    File outputFile = new File(temporaryDir, "Generated.swift")

    @TaskAction
    void generate() {
        // Simple source code generator
        outputFile.text = sourceFile
    }
}
