#if os(Linux)
    import Glibc
#else
    import Darwin
#endif

public class Console {
    public init() {
    }

    // Writes the given string using color text, if connected to a console
    public func color(_ s: String) {
        if (isatty(1) != 0) {
            print("\u{001B}[0;32m" + s + "\u{001B}[0m")
        } else {
            print(s)
        }
    }
}
