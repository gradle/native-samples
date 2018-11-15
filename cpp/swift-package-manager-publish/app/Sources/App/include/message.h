/*
 * A library for the message.
 */

#pragma once

#include <string>

#ifdef _WIN32
#  ifdef MESSAGE_MODULE_EXPORT
#    define MESSAGE_API __declspec(dllexport)
#  else
#    define MESSAGE_API __declspec(dllimport)
#  endif
#else
#  define MESSAGE_API
#endif

/*
 * Return the message.
 */
MESSAGE_API std::string get_message();
