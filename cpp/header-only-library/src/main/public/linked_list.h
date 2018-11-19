/*
 * A linked list implementation.
 */

#pragma once

template<typename T>
class node {
  public:
    node(const T & data) : next_(NULL), data_(data) {}

    node<T> * next() { return next_; }
    void set_next(node<T> * next) { next_ = next; }

    T & data() { return data_; }
  private:
    T data_;
    node<T> * next_;
};

template<typename T>
static void unlink(node<T> ** head, node<T> * previous_it, node<T> * current_it) {
    if (current_it == *head) {
        *head = current_it->next();
    } else {
        previous_it->set_next(current_it->next());
    }
}

template<typename T>
class linked_list {
  public:
    linked_list() : head_(NULL) {}
    
    // Copy constructor/assignment
    linked_list(const linked_list<T> & o) {
      head_ = NULL;
      copy(o, this);
    }
    linked_list & operator=(const linked_list<T> & rhs) {
      head_ = NULL;
      copy(rhs, this);
      return *this;
    }
    
    // Default destructor
    ~linked_list() /*noexcept*/ {
      for (node<T> *it = head_; it != NULL;) {
          node<T> *next_it = it->next();
          delete it;
          it = next_it;
      }
    }

    // Adds the given element to this list
    void add(const T & element) {
      node<T> * new_node = new node<T>(element);

      node<T> * it = tail(head_);
      if (NULL == it) {
          head_ = new_node;
      } else {
          it->set_next(new_node);
      }
    }

    // Removes the given element from this list, if present
    bool remove(const T & element) {
      bool result = false;
      node<T> * previous_it = NULL;
      node<T> * it = NULL;
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

    // Returns the size of this list
    std::size_t size() const {
      std::size_t size = 0;

      for (node<T> * it = head_; it != NULL; ++size, it = it->next()) {}

      return size;
    }

    // Returns the element at the given index
    T & get(std::size_t index) {
      const linked_list * const_this = this;
      return const_cast<T &>(const_this->get(index));
    }
    const T & get(std::size_t index) const {
      node<T> * it = head_;
      while (index > 0 && NULL != it) {
          it = it->next();
          index--;
      }

      if (NULL == it) {
          throw std::out_of_range(std::string("Index is out of range"));
      }

      return it->data();
    }
  private:
    node<T> * head_;
};

template<typename T>
static node<T> * tail(node<T> * head) {
    node<T> * it;

    for (it = head; it != NULL && it->next() != NULL; it = it->next()) {}

    return it;
}

template<typename T>
static void copy(const linked_list<T> & source, linked_list<T> * destination) {
    for (int i = 0; i < source.size(); ++i) {
        destination->add(source.get(i));
    }
}
