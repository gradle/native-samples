#include "common.h"
#include "bar/common.h"
#include <iostream>

int main(int argc, char** argv) {
    std::cout << "Value is " << foo() << "  " << bar_common() << std::endl;
    return 0;
}