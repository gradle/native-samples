cmake_minimum_required(VERSION 2.8)
project(app)

add_executable(app
    src/app/main.cpp)

include_directories(${INCLUDE_DIRS})
