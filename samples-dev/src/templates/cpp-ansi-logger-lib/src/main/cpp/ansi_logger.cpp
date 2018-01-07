#include "ansi_logger.h"

#ifndef _WIN32

#include <unistd.h>
#include <iostream>

ansi_logger::ansi_logger() {
}

void ansi_logger::info(const std::string & message) {
    if (isatty(STDOUT_FILENO)) {
        // Bold green text
        std::cout << "\x1B[32;1m" << message << "\x1B[0m" << std::endl;
    } else {
        // Normal text
        std::cout << message << std::endl;
    }
}

#endif // #ifndef _WIN32
