cmake_minimum_required(VERSION 2.8)
project(utilities)

add_library(utilities
    src/utilities/split.cpp
    src/utilities/join.cpp)

target_include_directories(utilities PUBLIC src/utilities/include)
include_directories(${INCLUDE_DIRS})
