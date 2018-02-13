#define LIST_MODULE_EXPORT
#include <stdexcept>

#include "linked_list.h"
#include "node.h"

std::string & linked_list::get(std::size_t index) {
    const linked_list * const_this = this;
    return const_cast<std::string &>(const_this->get(index));
}

const std::string & linked_list::get(std::size_t index) const {
    node * it = head_;
    while (index > 0 && NULL != it) {
        it = it->next();
        index--;
    }

    if (NULL == it) {
        throw std::out_of_range(std::string("Index is out of range"));
    }

    return it->data();
}
