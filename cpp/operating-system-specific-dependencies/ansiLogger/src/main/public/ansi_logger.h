/*
 * A logger that uses ANSI control sequences to generate color output.
 */
#pragma once

#include <string>

class ansi_logger {
public:
    ansi_logger();
    void info(const std::string & message);
};
