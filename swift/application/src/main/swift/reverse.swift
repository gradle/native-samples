internal func reverse(source: String) -> String {
    var result = ""
    for character in source.characters {
        result = "\(character)" + result
    }

    return result
}
