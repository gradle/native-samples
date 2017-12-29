/*
 * A linked list implementation.
 */

#pragma once

#include <string>

#ifdef _WIN32
#  ifdef LIST_MODULE_EXPORT
#    define LIST_API __declspec(dllexport)
#  else
#    define LIST_API __declspec(dllimport)
#  endif
#else
#  define LIST_API
#endif

class node;

class LIST_API linked_list {
  public:
    linked_list() : head_(NULL) {}
    
    // Copy constructor/assignment
    linked_list(const linked_list &);
    linked_list & operator=(const linked_list &);
    
    // Default destructor
    ~linked_list() /*noexcept*/;

    // Adds the given element to this list
    void add(const std::string & element);

    // Removes the given element from this list, if present
    bool remove(const std::string & element);

    // Returns the size of this list
    std::size_t size() const;

    // Returns the element at the given index
    std::string & get(std::size_t index);
    const std::string & get(std::size_t index) const;
  private:
    node * head_;
};
