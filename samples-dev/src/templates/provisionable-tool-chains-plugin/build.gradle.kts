plugins {
    groovy
    `java-gradle-plugin`
}

group = "org.gradle.samples"
version = "1.0-SNAPSHOT"

dependencies {
    implementation(gradleApi())
    implementation(localGroovy())
    implementation("org.apache.commons:commons-compress:1.16.1")
    implementation("commons-io:commons-io:2.6")
    implementation("commons-codec:commons-codec:1.11")
    implementation("org.tukaani:xz:1.0")
}

repositories {
    mavenCentral()
}

gradlePlugin {
    (plugins) {
        "provisionableToolChains" {
            id = "org.gradle.samples.provisionable-tool-chains"
            implementationClass = "org.gradle.samples.plugins.ProvisionableToolChainsPlugin"
        }
    }
}
