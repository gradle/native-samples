#include "common.h"
#include "foo/common.h"
#include <iostream>

int main(int argc, char** argv) {
    std::cout << "Value is " << foo() << "  " << foo_common() << std::endl;
//    std::cout << "Value is " << foo() << std::endl;
    return 0;
}