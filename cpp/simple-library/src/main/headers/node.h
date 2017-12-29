#pragma once

#include <string>

class node {
  public:
    node(const std::string & data) : next_(NULL), data_(data) {}

    node * next() { return next_; }
    void set_next(node * next) { next_ = next; }

    std::string & data() { return data_; }
  private:
    std::string data_;
    node * next_;
};
