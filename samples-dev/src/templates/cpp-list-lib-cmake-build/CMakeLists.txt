cmake_minimum_required(VERSION 2.8)
project(list)

add_library(list
    src/list/add.cpp
    src/list/copy_ctor_assign.cpp
    src/list/destructor.cpp
    src/list/get.cpp
    src/list/remove.cpp
    src/list/size.cpp)

target_include_directories(list PUBLIC src/list/include)
