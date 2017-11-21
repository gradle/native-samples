import XCTest
@testable import App

class AppTests: XCTestCase {
    public static var allTests = [
        ("testEqualsExpectedMessage", testEqualsExpectedMessage),
    ]

    func testEqualsExpectedMessage() {
        XCTAssertEqual("Hello, World!", getMessage())
    }

    func testNoExceptionThrown() {
        XCTAssertNoThrow(getMessage())
    }

    func testNoExpectedMessage() {
        XCTAssertNotEqual("Goodbye, World!", getMessage())
    }
}
