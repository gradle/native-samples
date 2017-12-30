/*
 * A library of string utility functions.
 */

#pragma once

#include <string>

#include "linked_list.h"

#ifdef _WIN32
#  ifdef UTILITIES_MODULE_EXPORT
#    define UTILITIES_API __declspec(dllexport)
#  else
#    define UTILITIES_API __declspec(dllimport)
#  endif
#else
#  define UTILITIES_API
#endif

/*
 * Splits the given string into a list of tokens. Tokens are separated by one or more whitespace characters.
 */
UTILITIES_API linked_list split(const std::string & source);

/*
 * Joins the list of tokens into a string, separated by space characters.
 */
UTILITIES_API std::string join(const linked_list & source);
