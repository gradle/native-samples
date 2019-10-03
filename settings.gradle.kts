include("samples-dev")

includeBuild("/Users/daniel/gradle/guides/guides/subprojects/gradle-guides-plugin")
includeBuild("samples-dev/plugins")
includeBuild("samples-dev/src/templates/custom-publication-plugin")
includeBuild("samples-dev/src/templates/build-wrapper-plugin")
includeBuild("samples-dev/src/templates/release-plugin")

pluginManagement {
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id.startsWith("org.gradle.samples.")) {
                useModule("org.gradle.samples.plugins:plugins:1.0")
            }
        }
    }
}

