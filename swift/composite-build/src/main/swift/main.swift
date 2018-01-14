/*
 * A simple hello world application. Uses a library to tokenize and join a string and prints the result.
 */
import Utilities

class App {
    class func normalize(_ s: String) -> String {
        let tokens = Util.split(s)
        return Util.join(tokens)
    }
}

#if swift(>=4.0)
print("Built for Swift 4")
#elseif swift(>=3.0)
print("Built for Swift 3")
#else
print("Built for unsupported Swift version")
#endif

print(App.normalize("  Hello,      World!  "))
