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
    id("org.gradle.samples") version "0.15.18"
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

/**
 * C Samples
 */
samples.create("cApplication") {
    sampleDirectory.set(file("c/application"))
    withGroovyDsl {
        archiveContent.from(fileTree(sampleDirectory).include("**/*.gradle"))
    }
    copySource {
        appProject(".").buildRoot()
        appProject(".").fromTemplate("c-app")
    }
}


/**
 * C++ Samples
 */
samples.create("cppApplication") {
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
samples.create("cppAutotoolsLibrary") {
    sampleDirectory.set(file("cpp/autotools-library"))
    withGroovyDsl {
        archiveContent.from(fileTree(sampleDirectory).include("**/*.gradle"))
    }
    copySource {
        appProject(".").buildRoot()
        appProject("app").fromTemplate("cpp-app-with-curl")
        appProject("app").fromTemplate(cppUtilsLib)
        appProject("app").fromTemplate(cppListLib)
        appProject("app").fromTemplate(cppMessageLib)
        appProject("app").fromTemplate("cpp-message-static")
        project("build-wrapper-plugin").buildRoot()
        project("build-wrapper-plugin").fromTemplate("build-wrapper-plugin")
    }
}
samples.create("cppBinaryDependencies") {
    sampleDirectory.set(file("cpp/binary-dependencies"))
    withGroovyDsl {
        archiveContent.from(fileTree(sampleDirectory).include("**/*.gradle"))
    }
    copySource {
        appProject(".").buildRoot()
        appProject(".").fromTemplate("cpp-app")
        appProject(".").fromTemplate(cppMessageLib)
        appProject(".").fromTemplate("cpp-message-static")
        appProject(".").fromTemplate(cppUtilsLib)
    }
}
samples.create("cppCmakeLibrary") {
    sampleDirectory.set(file("cpp/cmake-library"))
    withGroovyDsl {
        archiveContent.from(fileTree(sampleDirectory).include("**/*.gradle"))
    }
    copySource {
        project(".").buildRoot()
        appProject("app").fromTemplate("cpp-app")
        appProject("app").fromTemplate(cppMessageLib)
        appProject("app").fromTemplate("cpp-message-static")
        libProject("utilities").fromTemplate(cppUtilsLib)
        cmakeProject("list", "list").fromTemplate(cppListLib)
        project("build-wrapper-plugin").buildRoot()
        project("build-wrapper-plugin").fromTemplate("build-wrapper-plugin")
        project("custom-publication-plugin").buildRoot()
        project("custom-publication-plugin").fromTemplate("custom-publication-plugin")
    }
}
samples.create("cppCmakeSourceDependencies") {
    sampleDirectory.set(file("cpp/cmake-source-dependencies"))
    withGroovyDsl {
        archiveContent.from(fileTree(sampleDirectory).include("**/*.gradle"))
    }
    copySource {
        cmakeAppProject("app").buildRoot()
        cmakeAppProject("app").fromTemplate("cpp-app-cmake-build")
        cmakeAppProject("app").fromTemplate("cpp-app")
        cmakeAppProject("app").fromTemplate(cppMessageLib)
        cmakeAppProject("app").fromTemplate("cpp-message-static")
        cmakeProject("utilities").buildRoot()
        cmakeProject("utilities").fromTemplate("cpp-utilities-lib-cmake-build")
        cmakeProject("utilities").fromTemplate(cppUtilsLib)
        cmakeProject("list").buildRoot()
        cmakeProject("list").fromTemplate("cpp-list-lib-cmake-build")
        cmakeProject("list").fromTemplate(cppListLib)
        project("build-wrapper-plugin").buildRoot()
        project("build-wrapper-plugin").fromTemplate("build-wrapper-plugin")
    }
}
samples.create("cppCompositeBuild") {
    sampleDirectory.set(file("cpp/composite-build"))
    withGroovyDsl {
        archiveContent.from(fileTree(sampleDirectory).include("**/*.gradle"))
    }
    copySource {
        appProject(".").buildRoot()
        appProject(".").fromTemplate("cpp-app")
        appProject(".").fromTemplate(cppMessageLib)
        appProject(".").fromTemplate("cpp-message-static")
        libProject("utilities-library").fromTemplate(cppUtilsLib)
        libProject("list-library").fromTemplate(cppListLib)
    }
}
samples.create("cppDependencyOnUpstreamBranch") {
    sampleDirectory.set(file("cpp/dependency-on-upstream-branch"))
    withGroovyDsl {
        archiveContent.from(fileTree(sampleDirectory).include("**/*.gradle"))
    }
    copySource {
        appProject("app").buildRoot()
        appProject("app").fromTemplate("cpp-app")
        appProject("app").fromTemplate(cppMessageLib)
        appProject("app").fromTemplate("cpp-message-static")
        libProject("utilities-library").buildRoot()
        libProject("utilities-library").fromTemplate(cppUtilsLib)
        libProject("utilities-library").fromTemplate("cpp-utilities-lib-build")
        libProject("list-library").buildRoot()
        libProject("list-library").fromTemplate(cppListLib)
        libProject("list-library").fromTemplate("cpp-list-lib-build")
    }
}
samples.create("cppHeaderOnlyLibrary") {
    sampleDirectory.set(file("cpp/header-only-library"))
    withGroovyDsl {
        archiveContent.from(fileTree(sampleDirectory).include("**/*.gradle"))
    }
    copySource {
        libProject(".").buildRoot()
        libProject(".").fromTemplate(CppLibraryTemplate.of("cpp-lib-header-only", "list"))
    }
}
samples.create("cppInjectedPlugins") {
    sampleDirectory.set(file("cpp/injected-plugins"))
    withGroovyDsl {
        archiveContent.from(fileTree(sampleDirectory).include("**/*.gradle"))
    }
    copySource {
        appProject(".").buildRoot()
        appProject(".").fromTemplate("cpp-app")
        appProject(".").fromTemplate(cppMessageLib)
        appProject(".").fromTemplate("cpp-message-static")
        libProject("repos/utilities-library").fromTemplate(cppUtilsLib)
        libProject("repos/list-library").fromTemplate(cppListLib)
    }
}
samples.create("cppLibraryWithTests") {
    sampleDirectory.set(file("cpp/library-with-tests"))
    withGroovyDsl {
        archiveContent.from(fileTree(sampleDirectory).include("**/*.gradle"))
    }
    copySource {
        libProject(".").buildRoot()
        libProject(".").fromTemplate(cppListLib)
        project("build-wrapper-plugin").buildRoot()
        project("build-wrapper-plugin").fromTemplate("build-wrapper-plugin")
    }
}
samples.create("cppMultipleTargetMachines") {
    sampleDirectory.set(file("cpp/multiple-target-machines"))
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
samples.create("cppOperatingSystemSpecificDependencies") {
    sampleDirectory.set(file("cpp/operating-system-specific-dependencies"))
    withGroovyDsl {
        archiveContent.from(fileTree(sampleDirectory).include("**/*.gradle"))
    }
    copySource {
        project(".").buildRoot()
        appProject("app").fromTemplate("cpp-app-uses-logger")
        appProject("app").fromTemplate("cpp-logger-uses-console")
        appProject("app").fromTemplate(cppUtilsLib)
        appProject("app").fromTemplate(cppListLib)
        libProject("ansiConsole").fromTemplate(CppLibraryTemplate.of("cpp-ansi-console-lib", "ansi_console"))
        libProject("winConsole").fromTemplate(CppLibraryTemplate.of("cpp-windows-console-lib", "win_console"))
    }
}
samples.create("cppPrebuiltBinaries") {
    sampleDirectory.set(file("cpp/prebuilt-binaries"))
    withGroovyDsl {
        archiveContent.from(fileTree(sampleDirectory).include("**/*.gradle"))
    }
    copySource {
        appProject(".").buildRoot()
        appProject(".").fromTemplate("cpp-app")
        appProject(".").fromTemplate(cppMessageLib)
        appProject(".").fromTemplate("cpp-message-static")
        appProject(".").fromTemplate(cppUtilsLib)
    }
}
samples.create("cppPrecompiledHeaders") {
    sampleDirectory.set(file("cpp/precompiled-headers"))
    withGroovyDsl {
        archiveContent.from(fileTree(sampleDirectory).include("**/*.gradle"))
    }
    copySource {
        appProject(".").buildRoot()
        appProject(".").fromTemplate("cpp-app")
        appProject(".").fromTemplate("cpp-app-precompiled-header")
        appProject(".").fromTemplate(cppMessageLib)
        appProject(".").fromTemplate("cpp-message-static")
        appProject(".").fromTemplate(cppUtilsLib)
        appProject(".").fromTemplate(cppListLib)
    }
}
samples.create("cppProvisionableToolChains") {
    sampleDirectory.set(file("cpp/provisionable-tool-chains"))
    withGroovyDsl {
        archiveContent.from(fileTree(sampleDirectory).include("**/*.gradle"))
    }
    copySource {
        appProject(".").buildRoot()
        appProject(".").fromTemplate("cpp-app")
        appProject(".").fromTemplate(cppUtilsLib)
        appProject(".").fromTemplate(cppListLib)
        appProject(".").fromTemplate(cppMessageLib)
        appProject(".").fromTemplate("cpp-message-static")
        appProject(".").fromTemplate("cpp-provisionable-tool-chains-build")
    }
}
samples.create("cppPublishMacros") {
    sampleDirectory.set(file("cpp/publish-macros"))
    withGroovyDsl {
        archiveContent.from(fileTree(sampleDirectory).include("**/*.gradle"))
    }
    copySource {
        project(".").buildRoot()
        appProject("app").fromTemplate("cpp-app")
        appProject("app").fromTemplate(cppMessageLib)
        appProject("app").fromTemplate("cpp-message-static")
        appProject("app").fromTemplate("cpp-app-check-published-macros")
        libProject("utilities").fromTemplate(cppUtilsLib)
        libProject("list").fromTemplate(cppListLib)
    }
}
samples.create("cppSimpleLibrary") {
    sampleDirectory.set(file("cpp/simple-library"))
    withGroovyDsl {
        archiveContent.from(fileTree(sampleDirectory).include("**/*.gradle"))
    }
    copySource {
        libProject(".").buildRoot()
        libProject(".").fromTemplate(cppListLib)
    }
}
samples.create("cppSourceDependencies") {
    sampleDirectory.set(file("cpp/source-dependencies"))
    withGroovyDsl {
        archiveContent.from(fileTree(sampleDirectory).include("**/*.gradle"))
    }
    copySource {
        appProject(".").buildRoot()
        appProject(".").fromTemplate("cpp-app")
        appProject(".").fromTemplate(cppMessageLib)
        appProject(".").fromTemplate("cpp-message-static")
    }
}
samples.create("cppSourceGeneration") {
    sampleDirectory.set(file("cpp/source-generation"))
    withGroovyDsl {
        archiveContent.from(fileTree(sampleDirectory).include("**/*.gradle"))
    }
    copySource {
        project(".").buildRoot()
    }
}
samples.create("cppStaticLibrary") {
    sampleDirectory.set(file("cpp/static-library"))
    withGroovyDsl {
        archiveContent.from(fileTree(sampleDirectory).include("**/*.gradle"))
    }
    copySource {
        project(".").buildRoot()
        appProject("app").fromTemplate("cpp-app")
        appProject("app").fromTemplate(cppMessageLib)
        appProject("app").fromTemplate("cpp-message-static")
        staticLibProject("utilities").fromTemplate(cppUtilsLib)
        libProject("list").fromTemplate(cppListLib)
    }
}
samples.create("cppSwiftPackageManager") {
    sampleDirectory.set(file("cpp/swift-package-manager"))
    withGroovyDsl {
        archiveContent.from(fileTree(sampleDirectory).include("**/*.gradle"))
    }
    copySource {
        project(".").buildRoot()
        swiftPMProject(".", "App").fromTemplate("cpp-app")
        swiftPMProject(".", "App").fromTemplate(cppMessageLib)
        swiftPMProject(".", "App").fromTemplate("cpp-message-static")
        swiftPMProject(".", "Utilities").fromTemplate(cppUtilsLib)
        swiftPMProject(".", "List").fromTemplate(cppListLib)
    }
}
samples.create("cppSwiftPackageManagerPublish") {
    sampleDirectory.set(file("cpp/swift-package-manager-publish"))
    withGroovyDsl {
        archiveContent.from(fileTree(sampleDirectory).include("**/*.gradle"))
    }
    copySource {
        swiftPMProject("app", "App").fromTemplate("cpp-app")
        swiftPMProject("app", "App").fromTemplate(cppMessageLib)
        swiftPMProject("app", "App").fromTemplate("cpp-message-static")

        project("release-plugin").buildRoot()
        project("release-plugin").fromTemplate("release-plugin")

        libProject("list-library").buildRoot()
        libProject("list-library").noPrivateHeaderDir()
        libProject("list-library").fromTemplate("cpp-list-lib-build-with-release")
        libProject("list-library").fromTemplate(cppListLib)

        libProject("utilities-library").buildRoot()
        libProject("utilities-library").noPrivateHeaderDir()
        libProject("utilities-library").fromTemplate("cpp-utilities-lib-build-with-release")
        libProject("utilities-library").fromTemplate(cppUtilsLib)
    }
}
samples.create("cppTransitiveDependencies") {
    sampleDirectory.set(file("cpp/transitive-dependencies"))
    withGroovyDsl {
        archiveContent.from(fileTree(sampleDirectory).include("**/*.gradle"))
    }
    copySource {
        project(".").buildRoot()
        appProject("app").fromTemplate("cpp-app")
        appProject("app").fromTemplate(cppMessageLib)
        appProject("app").fromTemplate("cpp-message-static")
        libProject("utilities").fromTemplate(cppUtilsLib)
        libProject("list").fromTemplate(cppListLib)
    }
}
samples.create("cppWindowsResources") {
    sampleDirectory.set(file("cpp/windows-resources"))
    withGroovyDsl {
        archiveContent.from(fileTree(sampleDirectory).include("**/*.gradle"))
    }
    copySource {
        appProject(".").buildRoot()
        appProject(".").fromTemplate("cpp-app")
        appProject(".").fromTemplate(cppMessageLib)
        appProject(".").fromTemplate("cpp-message-resources")
        appProject(".").fromTemplate(cppUtilsLib)
        appProject(".").fromTemplate(cppListLib)
    }
}


/**
 * Swift Samples
 */
samples.create("swiftApplication") {
    sampleDirectory.set(file("swift/application"))
    withGroovyDsl {
        archiveContent.from(fileTree(sampleDirectory).include("**/*.gradle"))
    }
    copySource {
        appProject(".").buildRoot()
        appProject(".").fromTemplate("swift-app")
        appProject(".").fromTemplate(swiftUtilsLib)
        appProject(".").fromTemplate(swiftListLib)
    }
}
samples.create("swiftCompositeBuild") {
    sampleDirectory.set(file("swift/composite-build"))
    withGroovyDsl {
        archiveContent.from(fileTree(sampleDirectory).include("**/*.gradle"))
    }
    copySource {
        appProject(".").buildRoot()
        appProject(".").fromTemplate("swift-app")
        libProject("utilities-library").fromTemplate(swiftUtilsLib)
        libProject("list-library").fromTemplate(swiftListLib)
    }
}
samples.create("swiftCppDependencies") {
    sampleDirectory.set(file("swift/cpp-dependencies"))
    withGroovyDsl {
        archiveContent.from(fileTree(sampleDirectory).include("**/*.gradle"))
    }
    copySource {
        project(".").buildRoot()
        staticLibProject("list").fromTemplate("cpp-lib-with-c-api")
        appProject("app").fromTemplate(SwiftLibraryTemplate.of("swift-lib-uses-c-api", "List"))
        appProject("app").fromTemplate(swiftUtilsLib)
        appProject("app").fromTemplate("swift-app")
    }
}
samples.create("swiftDependencyOnUpstreamBranch") {
    sampleDirectory.set(file("swift/dependency-on-upstream-branch"))
    withGroovyDsl {
        archiveContent.from(fileTree(sampleDirectory).include("**/*.gradle"))
    }
    copySource {
        appProject("app").buildRoot()
        appProject("app").fromTemplate("swift-app")
        libProject("utilities-library").buildRoot()
        libProject("utilities-library").fromTemplate(swiftUtilsLib)
        libProject("utilities-library").fromTemplate("swift-utilities-lib-build")
        libProject("list-library").buildRoot()
        libProject("list-library").fromTemplate(swiftListLib)
        libProject("list-library").fromTemplate("swift-list-lib-build")
    }
}
samples.create("swiftInjectedPlugins") {
    sampleDirectory.set(file("swift/injected-plugins"))
    withGroovyDsl {
        archiveContent.from(fileTree(sampleDirectory).include("**/*.gradle"))
    }
    copySource {
        appProject(".").buildRoot()
        appProject(".").fromTemplate("swift-app")
        libProject("repos/utilities-library").fromTemplate(swiftUtilsLib)
        libProject("repos/list-library").fromTemplate(SwiftLibraryTemplate.of("swift-lib-unusable", "List"))
    }
}
samples.create("swiftIOSApplication") {
    sampleDirectory.set(file("swift/ios-application"))
    withGroovyDsl {
        archiveContent.from(fileTree(sampleDirectory).include("**/*.gradle"))
    }
    copySource {
        project(".").buildRoot()
        project("ios-plugin").buildRoot()
    }
}
samples.create("swiftMultipleTargetMachines") {
    sampleDirectory.set(file("swift/multiple-target-machines"))
    withGroovyDsl {
        archiveContent.from(fileTree(sampleDirectory).include("**/*.gradle"))
    }
    copySource {
        appProject(".").buildRoot()
        appProject(".").fromTemplate("swift-app")
        appProject(".").fromTemplate(swiftUtilsLib)
        appProject(".").fromTemplate(swiftListLib)
    }
}
samples.create("swiftOperatingSystemSpecificDependencies") {
    sampleDirectory.set(file("swift/operating-system-specific-dependencies"))
    withGroovyDsl {
        archiveContent.from(fileTree(sampleDirectory).include("**/*.gradle"))
    }
    copySource {
        project(".").buildRoot()
        appProject("app").fromTemplate("swift-app")
        appProject("app").fromTemplate("swift-app-uses-logger")
        appProject("app").fromTemplate("swift-logger-uses-console")
        appProject("app").fromTemplate(swiftUtilsLib)
        appProject("app").fromTemplate(swiftListLib)

        libProject("linux-console").fromTemplate(SwiftLibraryTemplate.of("swift-linux-console-lib", "LinuxLogger"))
        libProject("macos-console").fromTemplate(SwiftLibraryTemplate.of("swift-macos-console-lib", "MacOsLogger"))
    }
}
samples.create("swiftPrebuiltBinaries") {
    sampleDirectory.set(file("swift/prebuilt-binaries"))
    withGroovyDsl {
        archiveContent.from(fileTree(sampleDirectory).include("**/*.gradle"))
    }
    copySource {
        appProject(".").buildRoot()
        appProject(".").fromTemplate("swift-app")
        appProject(".").fromTemplate(swiftUtilsLib)
    }
}
samples.create("swiftProvisionableToolChains") {
    sampleDirectory.set(file("swift/provisionable-tool-chains"))
    withGroovyDsl {
        archiveContent.from(fileTree(sampleDirectory).include("**/*.gradle"))
    }
    copySource {
        appProject(".").buildRoot()
        appProject(".").fromTemplate("swift-app")
        appProject(".").fromTemplate(swiftUtilsLib)
        appProject(".").fromTemplate(swiftListLib)
        appProject(".").fromTemplate("swift-provisionable-tool-chains-build")
    }
}
samples.create("swiftSimpleLibrary") {
    sampleDirectory.set(file("swift/simple-library"))
    withGroovyDsl {
        archiveContent.from(fileTree(sampleDirectory).include("**/*.gradle"))
    }
    copySource {
        libProject(".").buildRoot()
        libProject(".").fromTemplate(swiftListLib)
    }
}
samples.create("swiftSourceDependencies") {
    sampleDirectory.set(file("swift/source-dependencies"))
    withGroovyDsl {
        archiveContent.from(fileTree(sampleDirectory).include("**/*.gradle"))
    }
    copySource {
        appProject(".").buildRoot()
        appProject(".").fromTemplate("swift-app")
    }
}
samples.create("swiftSourceGeneration") {
    sampleDirectory.set(file("swift/source-generation"))
    withGroovyDsl {
        archiveContent.from(fileTree(sampleDirectory).include("**/*.gradle"))
    }
    copySource {
        project(".").buildRoot()
    }
}
samples.create("swiftStaticLibrary") {
    sampleDirectory.set(file("swift/static-library"))
    withGroovyDsl {
        archiveContent.from(fileTree(sampleDirectory).include("**/*.gradle"))
    }
    copySource {
        project(".").buildRoot()
        appProject("app").fromTemplate("swift-app")
        libProject("utilities").fromTemplate(swiftUtilsLib)
        libProject("list").fromTemplate(swiftListLib)
    }
}
samples.create("swiftSwiftPackageManager") {
    sampleDirectory.set(file("swift/swift-package-manager"))
    withGroovyDsl {
        archiveContent.from(fileTree(sampleDirectory).include("**/*.gradle"))
    }
    copySource {
        project(".").buildRoot()
        swiftPMProject(".", "App").fromTemplate("swift-app")
        swiftPMProject(".", "Utilities").fromTemplate(swiftUtilsLib)
        swiftPMProject(".", "List").fromTemplate(swiftListLib)
    }
}
samples.create("swiftSwiftPackageManagerPublish") {
    sampleDirectory.set(file("swift/swift-package-manager-publish"))
    withGroovyDsl {
        archiveContent.from(fileTree(sampleDirectory).include("**/*.gradle"))
    }
    copySource {
        swiftPMProject("app", "App").fromTemplate("swift-app")

        project("release-plugin").buildRoot()
        project("release-plugin").fromTemplate("release-plugin")

        libProject("list-library").buildRoot()
        libProject("list-library").fromTemplate("swift-list-lib-build-with-release")
        libProject("list-library").fromTemplate(swiftListLib)

        libProject("utilities-library").buildRoot()
        libProject("utilities-library").fromTemplate("swift-utilities-lib-build-with-release")
        libProject("utilities-library").fromTemplate(swiftUtilsLib)
    }
}
samples.create("swiftSwiftVersions") {
    sampleDirectory.set(file("swift/swift-versions"))
    withGroovyDsl {
        archiveContent.from(fileTree(sampleDirectory).include("**/*.gradle"))
    }
    copySource {
        project(".").buildRoot()

        appProject("swift3-app").fromTemplate("swift-app")
        appProject("swift3-app").fromTemplate("swift-app-uses-logger")
        appProject("swift3-app").fromTemplate(SwiftLibraryTemplate.of("swift-logger-reports-swift-version", "Logger"))
        appProject("swift3-app").fromTemplate(SwiftLibraryTemplate.of("swift3-lib-with-api-dep", "Utilities"))
        appProject("swift3-app").fromTemplate(swiftListLib)

        appProject("swift4-app").fromTemplate("swift-app")
        appProject("swift4-app").fromTemplate("swift-app-uses-logger")
        appProject("swift4-app").fromTemplate(SwiftLibraryTemplate.of("swift-logger-reports-swift-version", "Logger"))
        appProject("swift4-app").fromTemplate(SwiftLibraryTemplate.of("swift4-lib-with-api-dep", "Utilities"))
        appProject("swift4-app").fromTemplate(swiftListLib)

        appProject("swift5-app").fromTemplate("swift-app")
        appProject("swift5-app").fromTemplate("swift-app-uses-logger")
        appProject("swift5-app").fromTemplate(SwiftLibraryTemplate.of("swift-logger-reports-swift-version", "Logger"))
        appProject("swift5-app").fromTemplate(SwiftLibraryTemplate.of("swift5-lib-with-api-dep", "Utilities"))
        appProject("swift5-app").fromTemplate(swiftListLib)
    }
}
samples.create("swiftSystemLibraryAsModule") {
    sampleDirectory.set(file("swift/system-library-as-module"))
    withGroovyDsl {
        archiveContent.from(fileTree(sampleDirectory).include("**/*.gradle"))
    }
    copySource {
        project(".").buildRoot()
    }
}
samples.create("swiftTransitiveDependencies") {
    sampleDirectory.set(file("swift/transitive-dependencies"))
    withGroovyDsl {
        archiveContent.from(fileTree(sampleDirectory).include("**/*.gradle"))
    }
    copySource {
        project(".").buildRoot()
        appProject("app").fromTemplate("swift-app")
        libProject("utilities").fromTemplate(swiftUtilsLib)
        libProject("list").fromTemplate(swiftListLib)
    }
}