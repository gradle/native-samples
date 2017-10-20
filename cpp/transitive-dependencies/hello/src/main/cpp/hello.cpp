#include "greeting.h"

#include "logger.h"

GREETING_API void sayHello() {
    log(getHello());
}
