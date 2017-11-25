import XCTest
@testable import App

class FailingTests: XCTestCase {
    public static var allTests = [
        ("testHasTypo", testHasTypo),
    ]

    func testHasTypo() {
        XCTAssertEqual("Bonjour, Monde!", getMessage())
    }
}
