plugins {
    `java-gradle-plugin`
}

group = "org.gradle.samples"
version = "1.0"

gradlePlugin {
    (plugins) {
        create("headerOnly") {
            id = "org.gradle.samples.cpp-header-library"
            implementationClass = "org.gradle.samples.plugins.CppHeaderLibraryPlugin"
        }
    }
}