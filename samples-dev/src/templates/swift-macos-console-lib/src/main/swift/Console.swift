#if os(macOS)
    import Darwin
#endif

public class Console {
    public init() {
    }

    // Writes the given string using color text, if connected to a console
    public func color(_ s: String) {
#if os(macOS)
        if (isatty(1) != 0) {
            // blue text
            print("\u{001B}[0;34m" + s + "\u{001B}[0m")
            return
        }
#endif
        print(s)
    }
}
