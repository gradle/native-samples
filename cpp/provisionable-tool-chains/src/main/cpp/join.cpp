#define UTILITIES_MODULE_EXPORT
#include "string_utils.h"

#include "linked_list.h"

UTILITIES_API std::string join(const linked_list & source) {
    std::string result;
    for (int i = 0; i < source.size(); ++i) {
        if (i > 0) {
            result.append(" ");
        }
        result.append(source.get(i));
    }

    return result;
}
