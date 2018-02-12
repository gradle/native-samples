import org.gradle.api.component.SoftwareComponentContainer
import org.gradle.kotlin.dsl.maven
import org.gradle.kotlin.dsl.repositories
import org.gradle.language.cpp.CppExecutable
import org.gradle.language.cpp.CppSharedLibrary
import org.gradle.api.publish.PublishingExtension

allprojects {
    apply { plugin("xcode") }
    apply { plugin("maven-publish") }

    group = "org.gradle.cpp-samples"
    version = "1.2"

    configure<PublishingExtension> {
        repositories {
            maven {
                url = uri("../../repo")
            }
        }
    }

    val assembleDebuggable by tasks.creating {
        group = "Build"
        description = "Assemble all debuggable C++ binaries."
        components.withType<CppExecutable> {
            if (isDebuggable()) {
                dependsOn(executableFile)
            }
        }
        components.withType<CppSharedLibrary> {
            if (isDebuggable()) {
               dependsOn(linkFile)
            }
        }
    }
}
