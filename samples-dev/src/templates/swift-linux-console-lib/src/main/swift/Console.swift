#if os(Linux)
    import Glibc
#endif

public class Console {
    public init() {
    }

    // Writes the given string using color text, if connected to a console
    public func color(_ s: String) {
#if os(Linux)
        if (isatty(1) != 0) {
            // green text
            print("\u{001B}[0;32m" + s + "\u{001B}[0m")
            return
        }
#endif
        print(s)
    }
}
