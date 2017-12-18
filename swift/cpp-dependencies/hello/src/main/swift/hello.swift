import greeting

public func sayHello() -> String {
    return String(cString: getHello())
}
