#include "linked_list.h"
#include "node.h"

static void copy(const linked_list & source, linked_list * destination) {
    for (int i = 0; i < source.size(); ++i) {
        destination->add(source.get(i));
    }
}

linked_list::linked_list(const linked_list & o) {
    copy(o, this);
}

linked_list & linked_list::operator=(const linked_list & rhs) {
    copy(rhs, this);
    return *this;
}
