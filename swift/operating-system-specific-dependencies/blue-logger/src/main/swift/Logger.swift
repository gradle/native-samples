public class Logger {
    public class func info(_ s: String) {
        print("\u{001B}[0;34m" + s)
    }
}
