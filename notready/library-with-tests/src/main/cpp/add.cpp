#define LIST_MODULE_EXPORT
#include "linked_list.h"

#include "node.h"

static node * tail(node * head) {
    node * it;

    for (it = head; it != NULL && it->next() != NULL; it = it->next()) {}

    return it;
}

void linked_list::add(const std::string & element) {
    node * new_node = new node(element);

    node * it = tail(head_);
    if (NULL == it) {
        head_ = new_node;
    } else {
        it->set_next(new_node);
    }
}
