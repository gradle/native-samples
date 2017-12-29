#include "linked_list.h"
#include <stdlib.h>
#include <string.h>
#include <iostream>

linked_list* empty_list() {
    linked_list* list = (linked_list*) malloc(sizeof(linked_list));
    list->head = NULL;
    list->tail = NULL;
    return list;
}

void list_delete(linked_list* list) {
    struct linked_list_node* n = list->head;
    while (n != NULL) {
        free((void*) n->data);
        struct linked_list_node* next = n->next;
        free(n);
        n = next;
    }
    free(list);
}

void list_add(linked_list* list, const char* str) {
    struct linked_list_node* n = (struct linked_list_node*)malloc(sizeof(struct linked_list_node));
    size_t l = strlen(str);
    char* copy = (char*) malloc(l+1);
    strncpy(copy, str, l+1);
    n->data = copy;
    n->next = NULL;

    if (list->head == NULL) {
        list->head = n;
        list->tail = n;
    } else {
        list->tail->next = n;
        list->tail = n;
    }
}

int list_size(linked_list* list) {
    int count = 0;
    struct linked_list_node* n = list->head;
    while (n != NULL) {
        count++;
        n = n->next;
    }
    return count;
}

const char* list_get(linked_list* list, int index) {
    int count = 0;
    struct linked_list_node* n = list->head;
    while (count < index && n != NULL) {
        count++;
        n = n->next;
    }
    return n != NULL ? n->data : NULL;
}
