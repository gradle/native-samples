#include "hello.h"

// `logger.h` is an implementation detail, aka private, headers of the hello project.
// It cannot be included here.
// #include "logger.h"

// Simple hello world app
int main() {
    sayHello();
    return 0;
}
