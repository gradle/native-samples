import XCTest
@testable import Hello

class AppTests: XCTestCase {
    public static var allTests = [
        ("testEqualsExpectedMessage", testEqualsExpectedMessage),
        ("testNoExceptionThrown", testNoExceptionThrown),
        ("testNoExpectedMessage", testNoExpectedMessage),
    ]

    func testEqualsExpectedMessage() {
        XCTAssertEqual("Hello World!", sayHello())
    }

    func testNoExceptionThrown() {
        XCTAssertNoThrow(sayHello())
    }

    func testNoExpectedMessage() {
        XCTAssertNotEqual("Goodbye, World!", sayHello())
    }
}
