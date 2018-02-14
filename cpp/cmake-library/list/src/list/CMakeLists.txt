project(listLib)

add_library(list
    add.cpp
    copy_ctor_assign.cpp
    destructor.cpp
    get.cpp
    remove.cpp
    size.cpp)

target_include_directories(list PUBLIC include)
