project(utilitiesLib VERSION 0.1 LANGUAGES CXX)

add_library(utilities 
    join.cpp
    split.cpp)

target_include_directories(utilities PUBLIC
    include)

target_link_libraries(utilities list)

install(TARGETS utilities EXPORT utilities
    ARCHIVE  DESTINATION "lib"
    LIBRARY  DESTINATION "lib"
    RUNTIME  DESTINATION "bin")

install(DIRECTORY include/ DESTINATION "include")