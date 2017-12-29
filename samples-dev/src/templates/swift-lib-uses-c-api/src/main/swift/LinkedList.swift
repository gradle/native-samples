/*
 * A Swift wrapper over a C linked list implementation.
 */
import list

public class LinkedList {
    let list: UnsafeMutablePointer<linked_list>

    public init() {
        list = empty_list()
    }

    deinit {
        list_delete(list)
    }

    public func add(_ s: String) {
        list_add(list, s)
    }

    public func get(_ i: Int) -> String {
        return String(cString: list_get(list, Int32(i)))
    }

    public func size() -> Int {
        return Int(list_size(list))
    }
}
