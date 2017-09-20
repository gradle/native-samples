import XCTest
import Greeter

class HelloTest: XCTestCase {
    func testCanInstantiateGreeterClass() {
        XCTAssertNotNil(Greeter())
    }

    func testCanSayHelloWithoutException() {
        let greeter = Greeter()
        XCTAssertNoThrow(greeter.sayHello())
    }
}
