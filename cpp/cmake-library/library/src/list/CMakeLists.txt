project(listLib VERSION 0.1 LANGUAGES CXX)

add_library(list 
    add.cpp
    copy_ctor_assign.cpp
    destructor.cpp
    get.cpp
    remove.cpp
    size.cpp)

target_include_directories(list PUBLIC
    include)

install(TARGETS list EXPORT list
    ARCHIVE  DESTINATION "lib"
    LIBRARY  DESTINATION "lib"
    RUNTIME  DESTINATION "bin")

install(DIRECTORY include/ DESTINATION "include")



