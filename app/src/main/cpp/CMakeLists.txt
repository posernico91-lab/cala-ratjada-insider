cmake_minimum_required(VERSION 3.22.1)
project("vivox_bridge")

# Find prebuilt Vivox native library from AAR
add_library(vivox-sdk SHARED IMPORTED)
set_target_properties(vivox-sdk PROPERTIES
    IMPORTED_LOCATION ${CMAKE_CURRENT_SOURCE_DIR}/../jniLibs/${ANDROID_ABI}/libvivox-sdk.so
)

# Our JNI bridge library
add_library(vivox_bridge SHARED vivox_bridge.cpp)

target_include_directories(vivox_bridge PRIVATE
    ${CMAKE_CURRENT_SOURCE_DIR}/include
)

find_library(log-lib log)

target_link_libraries(vivox_bridge
    vivox-sdk
    ${log-lib}
)
