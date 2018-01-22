/*
 * A logger that uses ANSI control sequences to generate color output.
 */
#pragma once

#include <string>

class ansi_console {
public:
    ansi_console();
    void color(const std::string & message);
};
