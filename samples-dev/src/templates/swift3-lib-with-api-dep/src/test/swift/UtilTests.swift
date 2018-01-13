import XCTest
import List
@testable import Utilities

class UtilTests: XCTestCase {
    public static var allTests = [
        ("testSplitEmpty", testSplitEmpty),
        ("testSplitWhitespace", testSplitWhitespace),
        ("testSplitWithNoSeparator", testSplitWithNoSeparator),
        ("testSplitWithSeparator", testSplitWithSeparator),
        ("testSplitWithMultipleSeparator", testSplitWithMultipleSeparator),
        ("testSplitWithTrailingSeparator", testSplitWithTrailingSeparator),
        ("testSplitWithLeadingSeparator", testSplitWithLeadingSeparator),
        ("testJoinEmpty", testJoin),
    ]

    func testSplitEmpty() {
        let l = Util.split("")
        XCTAssertEqual(0, l.size())
    }

    func testSplitWhitespace() {
        let l = Util.split("    ")
        XCTAssertEqual(0, l.size())
    }

    func testSplitWithNoSeparator() {
        let l = Util.split("abc")
        XCTAssertEqual(1, l.size())
        XCTAssertEqual("abc", l.get(0))
    }

    func testSplitWithSeparator() {
        let l = Util.split("abc def")
        XCTAssertEqual(2, l.size())
        XCTAssertEqual("abc", l.get(0))
        XCTAssertEqual("def", l.get(1))
    }

    func testSplitWithMultipleSeparator() {
        let l = Util.split("abc     def")
        XCTAssertEqual(2, l.size())
        XCTAssertEqual("abc", l.get(0))
        XCTAssertEqual("def", l.get(1))
    }

    func testSplitWithTrailingSeparator() {
        let l = Util.split("abc def    ")
        XCTAssertEqual(2, l.size())
        XCTAssertEqual("abc", l.get(0))
        XCTAssertEqual("def", l.get(1))
    }

    func testSplitWithLeadingSeparator() {
        let l = Util.split("   abc def")
        XCTAssertEqual(2, l.size())
        XCTAssertEqual("abc", l.get(0))
        XCTAssertEqual("def", l.get(1))
    }

    func testJoinEmpty() {
        let l = LinkedList()
        XCTAssertEqual("", Util.join(l))
    }

    func testJoin() {
        let l = LinkedList()
        l.add("abc")
        l.add("def")
        XCTAssertEqual("abc def", Util.join(l))
    }
}
