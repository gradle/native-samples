/*
 * Writes log messages to some destination.
 */
#pragma once

#include <string>

class logger {
public:
    logger();

    // Writes an info message and trailing new-line
    void info(const std::string & message);
};
