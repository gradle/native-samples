plugins {
    `cpp-library`
}

configure<CppLibrary> {
    linkage.set(listOf(Linkage.STATIC, Linkage.SHARED))
}
