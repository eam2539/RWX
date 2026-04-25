cmake_minimum_required(VERSION 3.16)

project(rocketConnector LANGUAGES CXX)

find_package(JNI REQUIRED)

set(BUILD_SHARED_LIBS OFF CACHE BOOL "" FORCE)
set(BUILD_SAMPLES OFF CACHE BOOL "" FORCE)
set(BUILD_LUA_BINDINGS OFF CACHE BOOL "" FORCE)
set(BUILD_PYTHON_BINDINGS OFF CACHE BOOL "" FORCE)
set(CMAKE_POSITION_INDEPENDENT_CODE ON)
add_subdirectory(${CMAKE_CURRENT_SOURCE_DIR}/../third_party/libRocket/Build ${CMAKE_CURRENT_BINARY_DIR}/libRocket)

add_library(rocketConnector SHARED
    src/rocket_connector.cpp
)

target_compile_features(rocketConnector PRIVATE cxx_std_17)
target_compile_definitions(rocketConnector PRIVATE ROCKET_STATIC_LIB)
target_include_directories(rocketConnector PRIVATE
    ${JNI_INCLUDE_DIRS}
    ${CMAKE_CURRENT_SOURCE_DIR}/../third_party/libRocket/Include
)
target_link_libraries(rocketConnector PRIVATE RocketDebugger RocketControls RocketCore ${JNI_LIBRARIES})

if(WIN32 AND CMAKE_SIZEOF_VOID_P EQUAL 8)
    set_target_properties(rocketConnector PROPERTIES OUTPUT_NAME rocketConnector64)
else()
    set_target_properties(rocketConnector PROPERTIES OUTPUT_NAME rocketConnector)
endif()

if(MSVC)
    target_compile_options(rocketConnector PRIVATE /W4)
else()
    target_compile_options(rocketConnector PRIVATE -Wall -Wextra -Wpedantic)
endif()
