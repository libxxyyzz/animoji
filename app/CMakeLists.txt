cmake_minimum_required(VERSION 3.4.1)


set(target animoji_jni)
add_library(${target}
        SHARED
        src/main/cpp/jni.cpp
        )
target_include_directories(${target} PRIVATE ${CMAKE_CURRENT_SOURCE_DIR}/libs/assimp/include)
target_include_directories(${target} PRIVATE ${CMAKE_BINARY_DIR})
link_directories(${CMAKE_CURRENT_SOURCE_DIR}/libs/libfacedetection/${CMAKE_ANDROID_ARCH_ABI})
target_link_libraries(${target}
        android
        log
        GLESv3
#        ${CMAKE_CURRENT_SOURCE_DIR}/libs/assimp/${CMAKE_ANDROID_ARCH_ABI}/libassimp.so
#        ${CMAKE_CURRENT_SOURCE_DIR}/libs/assimp/${CMAKE_ANDROID_ARCH_ABI}/libandroid_jniiosystem.a
        )

