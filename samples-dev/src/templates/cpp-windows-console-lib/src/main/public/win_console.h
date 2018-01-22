/*
 * A logger that uses the Windows console to generate color output.
 */
#pragma once

#include <string>

#define WIN_CONSOLE_API

class WIN_CONSOLE_API win_console {
public:
    win_console();
    void color(const std::string & message);
};
