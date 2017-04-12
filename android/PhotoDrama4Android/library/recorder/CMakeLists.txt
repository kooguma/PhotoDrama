cmake_minimum_required(VERSION 3.4.1)

set(distribution_DIR ${CMAKE_SOURCE_DIR}/../../../../libs)

add_library(
            ffmpeg
            SHARED
            IMPORTED )

set_target_properties(
            ffmpeg
            PROPERTIES IMPORTED_LOCATION
            ../../../../libs/ffmpeg/${ANDROID_ABI}/libffmpeg.so )

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=gnu++11 -D_CGE_USE_FFMPEG_ -DDEBUG -D__STDC_CONSTANT_MACROS")

include_directories(
    libs/ffmpeg/
)

add_library(
    loopeer
    SHARED
    libs/media/lprFFmpegNativeLibrary.cpp
    libs/media/lprVideoMuxer.cpp
)

target_link_libraries(
    loopeer
    ffmpeg
    log
    android)
