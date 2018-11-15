cmake_minimum_required(VERSION 2.8)
project(app)

add_executable(app
    src/app/main.cpp src/app/message.cpp)

include_directories(${INCLUDE_DIRS})

find_library(LIST_LIB list PATHS ${LINK_DIRS})
find_library(UTILITIES_LIB utilities PATHS ${LINK_DIRS})
target_link_libraries(app ${LIST_LIB} ${UTILITIES_LIB})
