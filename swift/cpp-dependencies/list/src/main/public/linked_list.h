/*
 * A linked list implementation.
 */

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

/*
 * Creates an empty list.
 */
linked_list* empty_list();

/*
 * Deletes the given list, freeing resources.
 */
void list_delete(linked_list* list);

/*
 * Adds a string to the end of the list. Takes a copy of the string.
 */
void list_add(linked_list* list, const char* str);

/*
 * Returns the current size of the list.
 */
int list_size(linked_list* list);

/*
 * Returns the string at the given index.
 */
const char* list_get(linked_list* list, int index);

#ifdef __cplusplus
}
#endif
