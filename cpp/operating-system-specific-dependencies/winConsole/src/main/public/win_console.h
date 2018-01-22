/*
 * A logger that uses the Windows console to generate color output.
 */
#pragma once

#include <string>

#ifdef _WIN32
#  ifdef WIN_CONSOLE_MODULE_EXPORT
#    define WIN_CONSOLE_API __declspec(dllexport)
#  else
#    define WIN_CONSOLE_API __declspec(dllimport)
#  endif
#else
#  define WIN_CONSOLE_API
#endif

class WIN_CONSOLE_API win_console {
public:
    win_console();
    void color(const std::string & message);
};
