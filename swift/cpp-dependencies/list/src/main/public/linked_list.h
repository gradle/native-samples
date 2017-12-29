#pragma once

#ifdef __cplusplus
extern "C" {
#endif

struct linked_list_node {
    const char* data;
    struct linked_list_node* next;
};

struct linked_list_t {
    struct linked_list_node* head;
    struct linked_list_node* tail;
};
typedef struct linked_list_t linked_list;

linked_list* empty_list();

void list_delete(linked_list* list);

void list_add(linked_list* list, const char* str);

int list_size(linked_list* list);

const char* list_get(linked_list* list, int index);

#ifdef __cplusplus
}
#endif
