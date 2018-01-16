/*
 * A simple hello world application. Uses a library to tokenize and join a string and prints the result.
 */
import Utilities
#if os(Linux)
    import LinuxLogger
#else
    import MacOsLogger
#endif

class App {
    class func normalize(_ s: String) -> String {
        let tokens = Util.split(s)
        return Util.join(tokens)
    }
}

let result = App.normalize("  Hello,      World!  ")
Logger.info(result)
