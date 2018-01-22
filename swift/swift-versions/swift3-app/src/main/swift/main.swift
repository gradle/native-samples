/*
 * A simple hello world application. Uses a library to tokenize and join a string and prints the result.
 */

class App {
    class func normalize(_ s: String) -> String {
        let tokens = Util.split(s)
        return Util.join(tokens)
    }
}

let logger = Logger()
let result = App.normalize("  Hello,      World!  ")
logger.info(result)
