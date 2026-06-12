cmake_minimum_required(VERSION 3.16)

if(POLICY CMP0144)
    cmake_policy(SET CMP0144 NEW)
endif()

project(rocketConnector LANGUAGES CXX)

find_package(JNI REQUIRED)

set(LibRocket_ROOT "$ENV{LIBROCKET_ROOT}" CACHE PATH "Root directory of libRocket source directory")
if(NOT LibRocket_ROOT)
    message(FATAL_ERROR "LibRocket_ROOT is not set. Please set the LIBROCKET_ROOT environment variable or use cmake -DLibRocket_ROOT=<path>")
endif()

list(APPEND CMAKE_MODULE_PATH "${CMAKE_CURRENT_LIST_DIR}")
find_package(LibRocket REQUIRED)
find_package(Freetype REQUIRED)

add_library(rocketConnector SHARED
    src/rocket_connector.cpp
)

target_compile_features(rocketConnector PRIVATE cxx_std_17)
target_include_directories(rocketConnector PRIVATE
    ${JNI_INCLUDE_DIRS}
    ${libRocket_INCLUDE_DIR}
)
target_link_libraries(rocketConnector PRIVATE
    ${libRocket_LIBRARIES}
    Freetype::Freetype
    ${JNI_LIBRARIES}
)

if(APPLE)
    set_target_properties(rocketConnector PROPERTIES BUILD_RPATH "@loader_path")
elseif(UNIX)
    set_target_properties(rocketConnector PROPERTIES BUILD_RPATH "$ORIGIN")
endif()

if(WIN32 AND CMAKE_SIZEOF_VOID_P EQUAL 8)
    set_target_properties(rocketConnector PROPERTIES OUTPUT_NAME rocketConnector64)
else()
    set_target_properties(rocketConnector PROPERTIES OUTPUT_NAME rocketConnector)
endif()

if(MSVC)
    target_compile_options(rocketConnector PRIVATE /W4 /wd4458)
else()
    target_compile_options(rocketConnector PRIVATE -Wall -Wextra -Wpedantic)
endif()
