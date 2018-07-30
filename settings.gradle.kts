include("samples-dev")

includeBuild("samples-dev/plugins")

pluginManagement {
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "org.gradle.samples.wrapper") {
                useModule("org.gradle.samples.plugins:generators:1.0")
            }
        }
    }
}
