plugins {
    id("java-gradle-plugin")
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
    implementation("org.eclipse.jgit:org.eclipse.jgit:5.0.2.201807311906-r")
    implementation("commons-io:commons-io:2.6")
    implementation("org.apache.commons:commons-lang3:3.8.1")
    implementation("com.google.guava:guava:27.1-jre")
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
