plugins {
    groovy
    `java-gradle-plugin`
}

group = "org.gradle.samples"
version = "1.0"

dependencies {
    implementation("commons-io:commons-io:2.6")
    implementation("com.googlecode.plist:dd-plist:1.20")
}

repositories {
    mavenCentral()
}

gradlePlugin {
    (plugins) {
        "ios-application" {
            id = "org.gradle.samples.ios-application"
            implementationClass = "org.gradle.samples.plugins.IOSApplicationPlugin"
        }
    }
}