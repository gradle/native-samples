#define LIST_MODULE_EXPORT
#include "linked_list.h"

#include "node.h"

static void unlink(node ** head, node * previous_it, node * current_it) {
    if (current_it == *head) {
        *head = current_it->next();
    } else {
        previous_it->set_next(current_it->next());
    }
}


bool linked_list::remove(const std::string & element) {
    bool result = false;
    node * previous_it = NULL;
    node * it = NULL;
    for (it = head_; !result && it != NULL; previous_it = it, it = it->next()) {
        if (0 == element.compare(it->data())) {
            result = true;
            unlink(&head_, previous_it, it);
            break;
        }
    }

    if (result) {
        delete it;
    }

    return result;
}
