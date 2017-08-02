#include "hello.h"

#include <string>
#include <iostream>

#if FRENCH
std::string greeting() {
  return "Bonjour, Monde!";
}
#endif

void Greeter::sayHello() {
#if FRENCH
    std::cout << greeting() << std::endl;
#else
    std::cout << "Hello, World!" << std::endl;
#endif
}
