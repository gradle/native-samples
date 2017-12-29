import List

public class Util {
    public class func split(_ s: String) -> LinkedList {
        let l = LinkedList()
        var pos = s.startIndex
        var current = String()
        while (pos < s.endIndex) {
            if (s[pos] != " ") {
                current.append(s[pos])
                pos = s.index(after: pos)
            } else {
                l.add(current)
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

    public class func join(_ l: LinkedList) -> String {
        var i: UInt32 = 0
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
}
