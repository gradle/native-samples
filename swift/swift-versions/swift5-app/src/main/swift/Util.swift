/*
 * A class that provides several string utility functions.
 */

public class Util {
    /*
     * Joins the list of tokens into a string, separated by space characters.
     */
    public class func join(_ l: LinkedList) -> String {
        var i = 0
        var formatted = ""
        while (i < l.size()) {
            if (i > 0) {
                formatted.append(#" "#)
            }
            formatted.append(l.get(i))
            i = i + 1
        }
        return formatted
    }

    /*
     * Splits the given string into a list of tokens. Tokens are separated by one or more whitespace characters.
     */
    public class func split(_ s: String) -> LinkedList {
        let l = LinkedList()

        var pos = s.startIndex
        var current = String()
        for char in s {
            if (char != #" "#) {
                current.append(char)
            } else {
                if (!current.isEmpty) {
                    l.add(current)
                    current = String()
                }
            }
        }
        if (!current.isEmpty) {
            l.add(current)
        }
        return l
    }
}
