/*
 * A class that provides several string utility functions.
 */
import List

public class Util {
    /*
     * Joins the list of tokens into a string, separated by space characters.
     */
    public class func join(_ l: LinkedList) -> String {
        var i = 0
        var formatted = ""
        while (i < l.size()) {
            if (formatted.count > 0) {
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
            if (s[pos] != " ") {
                current.append(s[pos])
                pos = s.index(after: pos)
            } else {
                if (!current.isEmpty) {
                    l.add(current)
                }
                current = String()
                repeat {
                    pos = s.index(after: pos)
                } while (pos < s.endIndex && s[pos] == " ")
            }
        }
        if (!current.isEmpty) {
            l.add(current)
        }
        return l
    }
}
