project(utilitiesLib LANGUAGES CXX)

add_library(utilities 
    join.cpp
    split.cpp
    add.cpp
    copy_ctor_assign.cpp
    destructor.cpp
    get.cpp
    join.cpp
    remove.cpp
    size.cpp)

target_include_directories(utilities PUBLIC
    include)