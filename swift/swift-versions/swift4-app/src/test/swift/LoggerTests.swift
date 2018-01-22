import XCTest
@testable import App

class LoggerTests: XCTestCase {
    public static var allTests = [
        ("testLogsMessage", testLogsMessage),
    ]

    func testLogsMessage() {
        let logger = Logger()
        logger.info("some message")
    }
}
