#include "win_console.h"

#ifdef _WIN32

#include <iostream>
#include <Windows.h>

win_console::win_console() {
}

void win_console::color(const std::string & message) {
    HANDLE console = GetStdHandle(STD_OUTPUT_HANDLE);
    if (console == INVALID_HANDLE_VALUE) {
        std::cout << message << std::endl;
    } else {
        // Bold green text
        CONSOLE_SCREEN_BUFFER_INFO screen_info;
        GetConsoleScreenBufferInfo(console, &screen_info);
        SetConsoleTextAttribute(console, (screen_info.wAttributes | FOREGROUND_INTENSITY | FOREGROUND_GREEN ) & ~FOREGROUND_RED & ~FOREGROUND_BLUE);
        std::cout << message << std::endl;
        SetConsoleTextAttribute(console, screen_info.wAttributes);
    }
}

#endif // #ifdef _WIN32
