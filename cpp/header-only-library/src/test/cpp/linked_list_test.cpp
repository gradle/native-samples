#include <string>

#include "gtest/gtest.h"
#include "linked_list.h"

TEST(LinkedListTests, test_constructor) {
    linked_list<std::string> list;
    ASSERT_EQ(list.size(), 0);
}

TEST(LinkedListTests, test_add) {
    linked_list<std::string> list;

    list.add("one");
    ASSERT_EQ(list.size(), 1);
    ASSERT_EQ(list.get(0), "one");

    list.add("two");
    ASSERT_EQ(list.size(), 2);
    ASSERT_EQ(list.get(1), "two");
}

TEST(LinkedListTests, test_remove) {
    linked_list<std::string> list;

    list.add("one");
    list.add("two");
    ASSERT_TRUE(list.remove("one"));

    ASSERT_EQ(list.size(), 1);
    ASSERT_EQ(list.get(0), "two");

    ASSERT_TRUE(list.remove("two"));
    ASSERT_EQ(list.size(), 0);
}

TEST(LinkedListTests, test_remove_missing) {
    linked_list<std::string> list;

    list.add("one");
    list.add("two");
    ASSERT_FALSE(list.remove("three"));
    ASSERT_EQ(list.size(), 2);
}
