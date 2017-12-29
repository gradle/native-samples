public class LinkedList {
    var head: Node?
    var tail: Node?

    public init() {
    }

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

    public func get(_ i: UInt32) -> String {
        var n = head
        var c = i
        while (c > 0 && n != nil) {
            n = n!.next
            c = c - 1
        }
        return n!.data
    }

    public func size() -> UInt32 {
        var c: UInt32 = 0
        var n = head
        while (n != nil) {
            c = c + 1
            n = n!.next
        }
        return c
    }
}

class Node {
    let data: String
    var next: Node?

    init(data: String) {
        self.data = data
    }
}