#include "gtest/gtest.h"
#include "linked_list.h"

TEST(LinkedListTests, test_constructor) {
    linked_list list;
    ASSERT_TRUE(list.size() == 0);
}

TEST(LinkedListTests, test_add) {
    linked_list list;

    list.add("one");
    ASSERT_TRUE(list.size() == 1);
    ASSERT_TRUE(list.get(0) == "one");

    list.add("two");
    ASSERT_TRUE(list.size() == 2);
    ASSERT_TRUE(list.get(1) == "two");
}

TEST(LinkedListTests, test_remove) {
    linked_list list;

    list.add("one");
    list.add("two");
    list.remove("one");

    ASSERT_TRUE(list.size() == 1);
    ASSERT_TRUE(list.get(0) == "two");

    list.remove("two");
    ASSERT_TRUE(list.size() == 0);
}
