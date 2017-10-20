#pragma once

#ifdef _WIN32
#  ifdef _DLL
#    define GREETER_API __declspec(dllexport)
#  else
#    define GREETER_API __declspec(dllimport)
#  endif
#else
#  define GREETER_API
#endif

class GREETER_API Greeter {
    public:
    void sayHello();
};
