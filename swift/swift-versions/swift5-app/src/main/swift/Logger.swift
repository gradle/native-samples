/*
 * A logger that reports the Swift version.
 */
class Logger {
    // Logs the Swift version
    init() {
#if swift(>=5.0)
        print("Built for Swift 5")
#elseif swift(>=4.0)
        print("Built for Swift 4")
#elseif swift(>=3.0)
        print("Built for Swift 3")
#else
        print("Built for unsupported Swift version")
#endif
    }

    // Writes an info message and trailing new-line
    func info(_ s: String) {
        print(s)
    }
}
