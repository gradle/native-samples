import XCTest
@testable import App

class AppTests: XCTestCase {
    public static var allTests = [
        ("testNormalizeEmpty", testNormalizeEmpty),
        ("testNormalize", testNormalize),
    ]

    func testNormalizeEmpty() {
        XCTAssertEqual("", App.normalize(""))
    }

    func testNormalize() {
        XCTAssertEqual("abc def", App.normalize("  abc  def  "))
    }
}
