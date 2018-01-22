#include "ansi_console.h"

#ifndef _WIN32

#include <unistd.h>
#include <iostream>

#ifdef linux
    // green text
    #define COLOR_ON "\x1B[0;32m"
#else
    // blue text
    #define COLOR_ON "\x1B[0;34m"
#endif

ansi_console::ansi_console() {
}

void ansi_console::color(const std::string & message) {
    if (isatty(STDOUT_FILENO)) {
        // Bold green text
        std::cout << COLOR_ON << message << "\x1B[0m" << std::endl;
    } else {
        // Normal text
        std::cout << message << std::endl;
    }
}

#endif // #ifndef _WIN32
