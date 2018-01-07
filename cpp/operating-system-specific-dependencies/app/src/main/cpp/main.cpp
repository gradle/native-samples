/*
 * A simple hello world application. Uses a library to tokenize and join a string and prints the result.
 */
#include <iostream>

#include "string_utils.h"
#include "linked_list.h"
#ifdef _WIN32
#include "win_logger.h"
#else
#include "ansi_logger.h"
#endif

int main() {
    linked_list tokens;
    tokens = split("Hello,      World!");
    std::string result = join(tokens);
#ifdef _WIN32
    win_logger logger;
#else
    ansi_logger logger;
#endif
    logger.info(result);
    return 0;
}
