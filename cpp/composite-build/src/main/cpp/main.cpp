#include <iostream>
#include "mathlib.h"
#include "string_utils.h"

// Simple hello world app
int main() {
    std::string value = "Hello,    World!";
    std::cout << "Convert '" << value << "' to use a single space: " << join(split(value)) << std::endl;
    Math math;
    std::cout << "The sum of 40 and 2 is " << math.sum(40, 2) << "!" << std::endl;
    return 0;
}
