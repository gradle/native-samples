cmake_minimum_required(VERSION 3.10)
project(libraries VERSION 0.1 LANGUAGES CXX)

add_subdirectory(src/utilities)

include (InstallRequiredSystemLibraries)
set (CPACK_PACKAGE_VERSION_MAJOR "${PROJECT_VERSION_MAJOR}")
set (CPACK_PACKAGE_VERSION_MINOR "${PROJECT_VERSION_MINOR}")
set (CPACK_GENERATOR "ZIP")
include (CPack)