import XCTest
@testable import App

class LinkedListTests: XCTestCase {
    public static var allTests = [
        ("testConstructor", testConstructor),
        ("testAdd", testAdd),
    ]

    func testConstructor() {
        let l = LinkedList()
        XCTAssertEqual(0, l.size())
    }

    func testAdd() {
        let l = LinkedList()

        l.add("one")
        XCTAssertEqual(1, l.size())
        XCTAssertEqual("one", l.get(0))

        l.add("two")
        XCTAssertEqual(2, l.size())
        XCTAssertEqual("one", l.get(0))
        XCTAssertEqual("two", l.get(1))
    }
}
