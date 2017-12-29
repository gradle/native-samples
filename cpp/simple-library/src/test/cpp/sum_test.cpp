#include "gtest/gtest.h"
#include "mathlib.h"

TEST(MathTests, test_sum) {
    Math* m = new Math();
    ASSERT_TRUE(m->sum(1, 2) == 3);
    ASSERT_TRUE(m->sum(3, -5) == -2);
    ASSERT_TRUE(m->sum(5, 5) == 10);
}
