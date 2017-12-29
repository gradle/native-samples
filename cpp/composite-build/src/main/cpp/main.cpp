#include <iostream>

#include "string_utils.h"
#include "linked_list.h"

// Simple hello world app
int main() {
    linked_list tokens;
    tokens = split("Hello,      World!");
    std::cout << join(tokens) << std::endl;
    return 0;
}
