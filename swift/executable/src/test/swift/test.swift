import XCTest
@testable import App

class MainTest: XCTestCase {
    func testEqualsExpectedMessage() {
        XCTAssertEqual("Hello, World!", getMessage())
    }
}
