/*
 * A logger that uses the Windows console to generate color output.
 */
#pragma once

#include <string>

#define WIN_LOGGER_API

class WIN_LOGGER_API win_logger {
public:
    win_logger();
    void info(const std::string & message);
};
