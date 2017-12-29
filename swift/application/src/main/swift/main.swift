/*
 * A simple hello world application. Uses a library to tokenize and join a string and prints the result.
 */

let tokens = Util.split("Hello,      World!")
print(Util.join(tokens))
