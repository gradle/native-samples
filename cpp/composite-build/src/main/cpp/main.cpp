#include <iostream>
#include "mathlib.h"
#include "hello.h"

// Simple hello world app
int main() {
    sayHello();
    Math math;
    std::cout << "The sum of 40 and 2 is " << math.sum(40, 2) << "!" << std::endl;
    return 0;
}
