#if FRENCH
func greeting() -> String {
  return "Bonjour, Monde!"
}
#endif


public class Greeter {
  public init() {}
  public func sayHello() {
    #if FRENCH
    print(greeting())
    #else
    print("Hello, World!")
    #endif
  }
}
