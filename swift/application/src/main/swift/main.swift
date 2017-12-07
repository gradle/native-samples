internal func getMessage() -> String {
    return "Hello, World!"
}

internal func printHelp() {
    print("usage: App \"string to reverse\"")
}

if (CommandLine.argc == 2) {
    let stringToReverse = CommandLine.arguments[1]
    print(getMessage())
    print("Here is the reverse string:")
    print(reverse(source: stringToReverse))
} else {
    if (CommandLine.argc > 2) {
        print("error: too many arguments")
    }
    printHelp()
}
