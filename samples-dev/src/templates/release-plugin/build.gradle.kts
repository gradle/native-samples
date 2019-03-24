plugins {
    `java-gradle-plugin`
}

group = "org.gradle.samples"
version = "1.0"

gradlePlugin {
    (plugins) {
        register("release") {
            id = "org.gradle.samples.release"
            implementationClass = "org.gradle.samples.ReleasePlugin"
        }
    }
}
