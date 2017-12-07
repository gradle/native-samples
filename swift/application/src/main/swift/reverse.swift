internal func reverse(source: String) -> String {
    let characters = Array(source)
    let reversedCharacters = characters.reversed()
    let reversedString = String(reversedCharacters)

    return reversedString
}
