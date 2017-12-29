/*
 * A linked list implementation.
 */

public class LinkedList {
    var head: Node?
    var tail: Node?

    public init() {
    }

    /*
     * Adds the given string to this list.
     */
    public func add(_ s: String) {
        let n = Node(data: s)
        if (head == nil) {
            head = n
            tail = head
        } else {
            tail!.next = n
            tail = n
        }
    }

    /*
     * Returns the string at the given index.
     */
    public func get(_ i: Int) -> String {
        var n = head
        var c = i
        while (c > 0 && n != nil) {
            n = n!.next
            c = c - 1
        }
        return n!.data
    }

    /*
     * Returns the size of this list.
     */
    public func size() -> Int {
        var c = 0
        var n = head
        while (n != nil) {
            c = c + 1
            n = n!.next
        }
        return c
    }
}
