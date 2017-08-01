public func getHello() -> String {
    #if FRENCH
    return "Bonjour, Monde!"
    #else
    return "Hello, World!"
    #endif
}
