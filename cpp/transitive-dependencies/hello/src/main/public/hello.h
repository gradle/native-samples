#pragma once

#ifdef _WIN32
#  ifdef HELLO_MODULE_EXPORT
#    define HELLO_API __declspec(dllexport)
#  else
#    define HELLO_API __declspec(dllimport)
#  endif
#else
#  define HELLO_API
#endif

HELLO_API void sayHello();
