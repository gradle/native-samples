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
                formatted.append(" ")
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
        while (pos < s.endIndex) {
            let nextPos = s.index(after: pos)
            let nextChar: String = s[pos..<nextPos]
            if (nextChar != " ") {
                current.append(nextChar)
            } else {
                if (!current.isEmpty) {
                    l.add(current)
                    current = String()
                }
            }
            pos = nextPos
        }
        if (!current.isEmpty) {
            l.add(current)
        }
        return l
    }
}
