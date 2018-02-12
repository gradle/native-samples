plugins {
    `cpp-library`
}

configure<CppLibrary> {
    dependencies {
        api(project(":list"))
    }
}
