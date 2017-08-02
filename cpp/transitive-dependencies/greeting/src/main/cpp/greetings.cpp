#include "greeting.h"

std::string getHello() {
#if FRENCH
    return "Bonjour, Monde!";
#else
    return "Hello, World!";
#endif
}
