import org.asciidoctor.gradle.AsciidoctorTask
import org.gradle.samples.ExemplarExtension
import org.gradle.samples.Sample
import org.gradle.samples.plugins.generators.CppLibraryTemplate
import org.gradle.samples.plugins.generators.SourceCopyTask
import org.gradle.samples.plugins.generators.SwiftLibraryTemplate
import org.gradle.util.GUtil

// This root project is simply a container of sample builds
plugins {
    id("org.gradle.samples.wrapper")
    id("org.gradle.samples")
}

tasks.withType<AsciidoctorTask>().matching { it.name.contains("Sample") }.configureEach {
    options(mapOf("doctype" to "book"))

    inputs.file("src/docs/css/manual.css")
            .withPropertyName("manual")
            .withPathSensitivity(PathSensitivity.RELATIVE)

    attributes(mapOf("stylesdir" to file("src/docs/css/").absolutePath,
            "stylesheet" to "manual.css",
            "nofooter" to true,
            "sectanchors" to true,
            "sectlinks" to true,
            "linkattrs" to true))
}

val cppUtilsLib = CppLibraryTemplate.of("cpp-lib-with-api-dep", "utilities")
val cppListLib = CppLibraryTemplate.of("cpp-lib", "list")
val cppMessageLib = CppLibraryTemplate.of("cpp-message-api", "message")

val swiftUtilsLib = SwiftLibraryTemplate.of("swift-lib-with-api-dep", "Utilities")
val swiftListLib = SwiftLibraryTemplate.of("swift-lib", "List")

val samples = project.extensions.getByName("samples") as NamedDomainObjectContainer<Sample>

fun Sample.copySource(configuration: SourceCopyTask.() -> kotlin.Unit) {
    val sample = this
    val copySourceTask = tasks.register("generate${GUtil.toCamelCase(sample.name)}Sample", SourceCopyTask::class.java) {
        val outputDir = project.layout.buildDirectory.dir("sample-generators/${sample.name}")
        sampleDir.set(outputDir)
        templatesDir.set(file("samples-dev/src/templates"))
    }
    copySourceTask.configure(configuration)

    val sourceContent = project.fileTree(copySourceTask.flatMap { it.sampleDir }) {
        builtBy(copySourceTask)

        // Patch over the stubbed Gradle wrappers
        exclude("gradlew*")
    }
    sample.archiveContent.from(sourceContent)
}

samples.configureEach {
    val sample = this
    val generatorTask = tasks.register("generate${GUtil.toCamelCase(sample.name)}") {
        val outputDir = project.layout.buildDirectory.dir("sample-exemplar-generators/${sample.name}")
        outputs.dir(outputDir)
        doLast {
            outputDir.get().file("helpTask.sample.conf").asFile.writeText("""
commands: [{
    execution-subdirectory: .
    executable: gradle
    args: help
}]
""")
            outputDir.get().file("taskList.sample.conf").asFile.writeText("""
commands: [{
    execution-subdirectory: .
    executable: gradle
    args: tasks
}]
""")
        }
    }
    sample.extensions.getByType(ExemplarExtension::class.java).source.from(generatorTask)
}

samples.create("cpp-application") {
    sampleDirectory.set(file("cpp/application"))
    withGroovyDsl {
        archiveContent.from(fileTree(sampleDirectory).include("**/*.gradle"))
    }
    copySource {
        appProject(".").buildRoot()
        appProject(".").fromTemplate("cpp-app")
        appProject(".").fromTemplate(cppMessageLib)
        appProject(".").fromTemplate("cpp-message-static")
        appProject(".").fromTemplate(cppUtilsLib)
        appProject(".").fromTemplate(cppListLib)
    }
}
