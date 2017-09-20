import XCTest
import Greeter

class SumTest: XCTestCase {
    func testCanSumNumbersTo42() {
        XCTAssertEqual(42, sum(a: 40, b: 2))
        XCTAssertEqual(42, sum(a: 12, b: 30))
        XCTAssertEqual(42, sum(a: 33, b: 9))
    }

    func testIncorrectSums() {
        XCTAssertNotEqual(42, sum(a: 11, b: 2))
        XCTAssertNotEqual(42, sum(a: 1, b: 2))
    }

    func testSumIsPerformant() {
        self.measure {
            sum(a: 40, b: 2)
        }
    }
}
