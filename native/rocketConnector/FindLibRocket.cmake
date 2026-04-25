# FindLibRocket.cmake
# CMake module to find libRocket libraries

if(NOT DEFINED LIBROCKET_ROOT)
    if(DEFINED ENV{LIBROCKET_ROOT})
        set(LIBROCKET_ROOT $ENV{LIBROCKET_ROOT})
    else()
        message(FATAL_ERROR "LIBROCKET_ROOT is not set. Please set the LIBROCKET_ROOT environment variable or use cmake -DLIBROCKET_ROOT=<path>")
    endif()
endif()

# Search paths - check Build subdir (where cmake outputs libraries), then root
set(LIBROCKET_SEARCH_PATHS
    "${LIBROCKET_ROOT}/Build"
    "${LIBROCKET_ROOT}"
)

# Find include directory
find_path(libRocket_INCLUDE_DIR
    NAMES Rocket/Core.h
    HINTS ${LIBROCKET_SEARCH_PATHS}
    PATH_SUFFIXES include Include
)

# Find libraries
find_library(libRocket_Core_LIBRARY
    NAMES RocketCore libRocketCore
    HINTS ${LIBROCKET_SEARCH_PATHS}
    PATH_SUFFIXES lib Lib
)
find_library(libRocket_Controls_LIBRARY
    NAMES RocketControls libRocketControls
    HINTS ${LIBROCKET_SEARCH_PATHS}
    PATH_SUFFIXES lib Lib
)
find_library(libRocket_Debugger_LIBRARY
    NAMES RocketDebugger libRocketDebugger
    HINTS ${LIBROCKET_SEARCH_PATHS}
    PATH_SUFFIXES lib Lib
)

include(FindPackageHandleStandardArgs)
find_package_handle_standard_args(LibRocket
    REQUIRED_VARS
        libRocket_INCLUDE_DIR
        libRocket_Core_LIBRARY
        libRocket_Controls_LIBRARY
        libRocket_Debugger_LIBRARY
)

if(LibRocket_FOUND)
    set(libRocket_LIBRARIES
        ${libRocket_Debugger_LIBRARY}
        ${libRocket_Controls_LIBRARY}
        ${libRocket_Core_LIBRARY}
    )
endif()
