/*
 * A logger that uses the Windows console to generate color output.
 */
#pragma once

#include <string>

#ifdef _WIN32
#  ifdef WIN_LOGGER_MODULE_EXPORT
#    define WIN_LOGGER_API __declspec(dllexport)
#  else
#    define WIN_LOGGER_API __declspec(dllimport)
#  endif
#else
#  define WIN_LOGGER_API
#endif

class WIN_LOGGER_API win_logger {
public:
    win_logger();
    void info(const std::string & message);
};
