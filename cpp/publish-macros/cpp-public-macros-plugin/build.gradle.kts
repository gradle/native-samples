plugins {
    `java-gradle-plugin`
}

group = "org.gradle.samples"
version = "1.0"

gradlePlugin {
    (plugins) {
        register("cppPublicMacros") {
            id = "org.gradle.samples.cpp-public-macros"
            implementationClass = "org.gradle.samples.plugins.CppPublicMacrosPlugin"
        }
    }
}