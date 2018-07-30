plugins {
    id("java-library")
    id("groovy")
    id("java-gradle-plugin")
    id("org.jetbrains.kotlin.jvm").version("1.2.51")
}

group = "org.gradle.samples.plugins"
java {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

repositories {
    jcenter()
}

dependencies {
    implementation(gradleApi())
    implementation(localGroovy())
    implementation("org.eclipse.jgit:org.eclipse.jgit:4.9.1.201712030800-r")
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
}

gradlePlugin {
    plugins {
        create("wrapper") {
            id = "org.gradle.samples.wrapper"
            implementationClass = "org.gradle.samples.plugins.wrapper.WrapperPlugin"
        }
        create("generator") {
            id = "org.gradle.samples.generators"
            implementationClass = "org.gradle.samples.plugins.generators.GeneratorPlugin"
        }
    }
}
