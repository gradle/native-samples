plugins {
    `cpp-application`
}

configure<CppApplication> {
    dependencies {
        implementation(project(":utilities"))
    }
}
