/*
 * A logger that writes messages to a console.
 */
#include <iostream>

#ifdef _WIN32
#include "win_console.h"
#else
#include "ansi_console.h"
#endif
#include "logger.h"

logger::logger() {
}

void logger::info(const std::string & message) {
#ifdef _WIN32
    win_console console;
#else
    ansi_console console;
#endif
    console.color(message);
}
