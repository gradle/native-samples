#include <iostream>

#include "string_utils.h"

// 'linked_list.h' is exposed through transitivity.
#include "linked_list.h"

// `node.h` is an implementation detail, aka private, headers of the linked_list project.
// It cannot be included here.
// #include "node.h"

// Simple hello world app
int main() {
    linked_list tokens;
    tokens = split("Hello,      World!");
    std::cout << join(tokens) << std::endl;
    return 0;
}
