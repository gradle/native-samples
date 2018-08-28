plugins {
    `java-gradle-plugin`
}

group = "org.gradle.samples"
version = "1.0"

gradlePlugin {
    (plugins) {
        "softwareModelBridge" {
            id = "org.gradle.samples.software-model-bridge"
            implementationClass = "org.gradle.samples.plugins.SoftwareModelBridgePlugin"
        }
    }
}
