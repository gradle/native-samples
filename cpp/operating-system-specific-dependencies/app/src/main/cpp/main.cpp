/*
 * A simple hello world application. Uses a library to tokenize and join a string and prints the result.
 */
#include <iostream>

#include "string_utils.h"
#include "linked_list.h"
#include "logger.h"

int main() {
    linked_list tokens;
    tokens = split("Hello,      World!");
    std::string result = join(tokens);
    logger logger;
    logger.info(result);
    return 0;
}
