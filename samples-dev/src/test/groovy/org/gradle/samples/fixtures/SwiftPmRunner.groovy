package org.gradle.samples.fixtures

class SwiftPmRunner {
    private File projectDir = new File(".").canonicalFile
    private List<String> args = []

    static SwiftPmRunner create() {
        return new SwiftPmRunner()
    }

    SwiftPmRunner withProjectDir(File projectDir) {
        this.projectDir = projectDir.canonicalFile
        return this
    }

    SwiftPmRunner withArguments(String... args) {
        this.args.clear()
        this.args.addAll(args as List)
        return this
    }

    void build() {
        def builder = new ProcessBuilder()
        builder.command(["swift"] + args)
        println "Running " + builder.command()
        builder.directory(projectDir)
        builder.redirectErrorStream(true)
        def process = builder.start()
        process.outputStream.close()
        def output = process.inputStream.text
        println output
        int exitCode = process.waitFor()
        if (exitCode != 0) {
            throw new AssertionError("Swift PM exited with non-zero exit code. Output:\n${output}")
        }
    }
}
