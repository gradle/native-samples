import XCTest
import Math

class SumTest: XCTestCase {
    func testCanSumNumbersTo42() {
        let main = Math()
        XCTAssertEqual(42, main.sum(a: 40, b: 2))
        XCTAssertEqual(42, main.sum(a: 12, b: 30))
        XCTAssertEqual(42, main.sum(a: 33, b: 9))
    }

    func testIncorrectSums() {
        let main = Math()
        XCTAssertNotEqual(42, main.sum(a: 11, b: 2))
        XCTAssertNotEqual(42, main.sum(a: 1, b: 2))
    }

    func testSumIsPerformant() {
        let main = Math()
        self.measure {
            main.sum(a: 40, b: 2)
        }
    }
}
