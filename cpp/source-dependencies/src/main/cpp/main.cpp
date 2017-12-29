/*
 * A simple hello world application. Uses a library to tokenize and join a string and prints the result.
 */
#include <iostream>

#include "string_utils.h"
#include "linked_list.h"

int main() {
    linked_list tokens;
    tokens = split("Hello,      World!");
    std::cout << join(tokens) << std::endl;
    return 0;
}
