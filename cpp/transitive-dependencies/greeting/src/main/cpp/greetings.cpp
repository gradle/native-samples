#define GREETING_MODULE_EXPORT
#include "greeting.h"

GREETING_API std::string getHello() {
    return "Hello, World!";
}
