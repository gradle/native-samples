#define LIST_MODULE_EXPORT
#include "linked_list.h"
#include "node.h"

linked_list::~linked_list() {
    for (node *it = head_; it != NULL;) {
        node *next_it = it->next();
        delete it;
        it = next_it;
    }
}
