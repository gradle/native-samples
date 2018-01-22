import XCTest
@testable import Logger

class LoggerTests: XCTestCase {
    public static var allTests = [
        ("testLogsMessage", testLogsMessage),
    ]

    func testLogsMessage() {
        let logger = Logger()
        logger.info("some message")
    }
}
