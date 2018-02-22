plugins {
    `cpp-library`
}

configure<CppLibrary> {
    linkage.set(listOf(Linkage.STATIC))

    dependencies {
        api(project(":list"))
    }
}
