#pragma once

#include <string>

#ifdef _WIN32
#  ifdef GREETING_MODULE_EXPORT
#    define GREETING_API __declspec(dllexport)
#  else
#    define GREETING_API __declspec(dllimport)
#  endif
#else
#  define GREETING_API
#endif

GREETING_API std::string getHello();
