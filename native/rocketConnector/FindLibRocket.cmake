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
    "${LibRocket_ROOT}/Build"
    "${LibRocket_ROOT}"
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
