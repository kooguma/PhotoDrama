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


add_library(
            libCGE
            SHARED
            IMPORTED )

set_target_properties(
            libCGE
            PROPERTIES IMPORTED_LOCATION
            ../../../../libs/cge/${ANDROID_ABI}/libCGE.so )


set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=gnu++11 -D_CGE_USE_FFMPEG_ -DDEBUG -D__STDC_CONSTANT_MACROS")

include_directories(
    libs/include/
    libs/ffmpeg/
)

add_library(
    loopeer
    SHARED
    libs/interface/cgeVideoPlayer.cpp
    libs/interface/cgeVideoEncoder.cpp
    libs/interface/cgeVideoDecoder.cpp
    libs/interface/cgeUtilFunctions.cpp
    libs/interface/cgeVideoDecoder.cpp
    libs/interface/cgeImageHandlerWrapper.cpp
    libs/interface/cgeImageHandlerAndroid.cpp
    libs/interface/cgeNativeLibrary.cpp
    libs/interface/cgeSharedGLContext.cpp
    libs/interface/cgeImageFilterWrapper.cpp
    libs/interface/cgeFrameRendererWrapper.cpp
    libs/interface/cgeFrameRenderer.cpp
    libs/interface/cgeFrameRecorderWrapper.cpp
    libs/interface/cgeFrameRecorder.cpp
    libs/interface/cgeFFmpegNativeLibrary.cpp
)

target_link_libraries(
    loopeer
    libCGE
    ffmpeg
    jnigraphics
    GLESv2
    EGL
    log
    android)
