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

UTILITIES_API linked_list split(const std::string & source);

UTILITIES_API std::string join(const linked_list & source);


