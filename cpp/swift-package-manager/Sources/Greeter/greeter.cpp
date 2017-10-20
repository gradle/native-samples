#define GREETER_MODULE_EXPORT
#include "greeter.h"
#include <iostream>

void Greeter::sayHello() {
    std::cout << "Hello world" << std::endl;
}
