#include "greeting.h"

#include "logger.h"

GREETER_API void sayHello() {
    log(getHello());
}
