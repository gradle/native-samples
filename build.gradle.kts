import org.asciidoctor.gradle.AsciidoctorTask

// This root project is simply a container of sample builds
plugins {
    `java-library`
    `kotlin-dsl`
    id("org.asciidoctor.convert") version "1.5.3"
    id("org.gradle.samples.wrapper")
}

repositories {
    jcenter()
    maven { url = uri("https://repo.gradle.org/gradle/libs-releases") }
}

dependencies {
    testImplementation("org.gradle:sample-check:0.7.0")
    testImplementation(gradleTestKit())
    asciidoctor("org.gradle:docs-asciidoctor-extensions:0.6.0")
}

tasks {
    getByName<AsciidoctorTask>("asciidoctor") {
        inputs.dir("cpp/application")
        sourceDir = file("cpp/application")
        outputDir = file("docs")
        backends("html5")

        attributes(
                mapOf("source-highlighter" to "prettify",
                        "imagesdir" to "images",
                        "stylesheet" to null,
                        "linkcss" to true,
                        "docinfodir" to ".",
                        "docinfo1" to "",
                        "nofooter" to true,
                        "icons" to "font",
                        "sectanchors" to true,
                        "sectlinks" to true,
                        "linkattrs" to true,
                        "encoding" to "utf-8",
                        "idprefix" to "",
                        "toc" to "auto",
                        "toclevels" to 1)
        )
    }
}
