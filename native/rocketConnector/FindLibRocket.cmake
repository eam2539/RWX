# FindLibRocket.cmake
# CMake module to find libRocket libraries

if(NOT DEFINED LibRocket_ROOT)
    if(DEFINED LIBROCKET_ROOT)
        set(LibRocket_ROOT "${LIBROCKET_ROOT}")
    elseif(DEFINED ENV{LIBROCKET_ROOT})
        set(LibRocket_ROOT "$ENV{LIBROCKET_ROOT}")
    endif()
endif()

if(NOT DEFINED LibRocket_ROOT)
    message(FATAL_ERROR "LibRocket_ROOT is not set. Please set the LIBROCKET_ROOT environment variable or use cmake -DLibRocket_ROOT=<path>")
endif()

# Search paths - check Build subdir (where cmake outputs libraries), then root
set(LIBROCKET_SEARCH_PATHS
    "${LibRocket_ROOT}/Build/build"
    "${LibRocket_ROOT}/Build/build/Release"
)

# Find include directory
find_path(libRocket_INCLUDE_DIR
    NAMES Rocket/Core.h
    HINTS ${LIBROCKET_SEARCH_PATHS}
    PATH_SUFFIXES include Include
)

# Find shared libraries
set(_libRocket_ORIGINAL_FIND_LIBRARY_SUFFIXES ${CMAKE_FIND_LIBRARY_SUFFIXES})
if(WIN32)
    set(CMAKE_FIND_LIBRARY_SUFFIXES .dll.a .lib)
    set(_libRocket_SHARED_LIBRARY_PATTERN "\\.(dll\\.a|lib)$")
elseif(APPLE)
    set(CMAKE_FIND_LIBRARY_SUFFIXES .dylib)
    set(_libRocket_SHARED_LIBRARY_PATTERN "\\.dylib$")
else()
    set(CMAKE_FIND_LIBRARY_SUFFIXES .so)
    set(_libRocket_SHARED_LIBRARY_PATTERN "\\.so(\\.[0-9]+)*$")
endif()

function(_libRocket_find_shared_library output_var)
    if(DEFINED ${output_var})
        set(_libRocket_EXISTING_LIBRARY "${${output_var}}")
        set(_libRocket_CLEAR_EXISTING_LIBRARY FALSE)
        if(_libRocket_EXISTING_LIBRARY AND NOT _libRocket_EXISTING_LIBRARY MATCHES "${_libRocket_SHARED_LIBRARY_PATTERN}")
            set(_libRocket_CLEAR_EXISTING_LIBRARY TRUE)
        elseif(_libRocket_EXISTING_LIBRARY)
            get_filename_component(_libRocket_ROOT_ABSOLUTE "${LibRocket_ROOT}" ABSOLUTE)
            file(RELATIVE_PATH _libRocket_EXISTING_LIBRARY_RELATIVE
                "${_libRocket_ROOT_ABSOLUTE}"
                "${_libRocket_EXISTING_LIBRARY}"
            )
            if(_libRocket_EXISTING_LIBRARY_RELATIVE MATCHES "^\\.\\.(/|$)")
                set(_libRocket_CLEAR_EXISTING_LIBRARY TRUE)
            endif()
        endif()
        if(_libRocket_CLEAR_EXISTING_LIBRARY)
            unset(${output_var} CACHE)
            unset(${output_var})
        endif()
        unset(_libRocket_CLEAR_EXISTING_LIBRARY)
    endif()

    find_library(${output_var}
        NAMES ${ARGN}
        HINTS ${LIBROCKET_SEARCH_PATHS}
        PATH_SUFFIXES lib Lib
    )
endfunction()

_libRocket_find_shared_library(libRocket_Core_LIBRARY RocketCore libRocketCore)
_libRocket_find_shared_library(libRocket_Controls_LIBRARY RocketControls libRocketControls)
_libRocket_find_shared_library(libRocket_Debugger_LIBRARY RocketDebugger libRocketDebugger)

set(CMAKE_FIND_LIBRARY_SUFFIXES ${_libRocket_ORIGINAL_FIND_LIBRARY_SUFFIXES})
unset(_libRocket_ORIGINAL_FIND_LIBRARY_SUFFIXES)
unset(_libRocket_SHARED_LIBRARY_PATTERN)

include(FindPackageHandleStandardArgs)
find_package_handle_standard_args(LibRocket
    REQUIRED_VARS
        libRocket_INCLUDE_DIR
        libRocket_Core_LIBRARY
        libRocket_Controls_LIBRARY
        libRocket_Debugger_LIBRARY
    REASON_FAILURE_MESSAGE "Shared libRocket libraries are required. Build libRocket with BUILD_SHARED_LIBS=ON and pass its root through LIBROCKET_ROOT or -DLibRocket_ROOT=<path>."
)

if(LibRocket_FOUND)
    set(libRocket_LIBRARIES
        ${libRocket_Debugger_LIBRARY}
        ${libRocket_Controls_LIBRARY}
        ${libRocket_Core_LIBRARY}
    )
endif()
