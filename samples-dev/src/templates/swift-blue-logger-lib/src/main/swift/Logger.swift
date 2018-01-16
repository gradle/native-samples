#if os(Linux)
    import Glibc
#else
    import Darwin
#endif

public class Logger {
    public class func info(_ s: String) {
        if (isatty(1) != 0) {
            print("\u{001B}[0;34m" + s + "\u{001B}[0m")
        } else {
            print(s)
        }
    }
}
