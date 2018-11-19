#define UTILITIES_MODULE_EXPORT
#include "string_utils.h"

#include "linked_list.h"

static bool is_token_valid(const std::string & token) {
    return !token.empty();
}

static void add_if_valid(const std::string & token, linked_list * list) {
    if (is_token_valid(token)) {
        list->add(token);
    }
}

UTILITIES_API linked_list split(const std::string & source) {
    std::string::size_type last_find = 0;
    std::string::size_type current_find = 0;
    linked_list result;

    while (std::string::npos != (current_find = source.find(" ", last_find))) {
        std::string token = source.substr(last_find);
        if (std::string::npos != current_find) {
            token = token.substr(0, current_find - last_find);
        }

        add_if_valid(token, &result);
        last_find = current_find + 1;
    }
    
    std::string token = source.substr(last_find);
    add_if_valid(token, &result);
    
    return result;
}
