cmake_minimum_required(VERSION 3.4.1)


set(target animoji_jni)
add_library(${target}
        SHARED
        src/main/cpp/jni.cpp
        src/main/cpp/jni_mesh.cpp
        src/main/cpp/jni_vao.cpp
        )
target_include_directories(
        ${target} PRIVATE
        ${CMAKE_CURRENT_SOURCE_DIR}/libs/assimp/include
        ${CMAKE_CURRENT_SOURCE_DIR}/libs/eos/include
        ${CMAKE_CURRENT_SOURCE_DIR}/libs/eos/3rdparty
)
target_include_directories(${target} PRIVATE ${CMAKE_BINARY_DIR})
link_directories(${CMAKE_CURRENT_SOURCE_DIR}/libs/libfacedetection/${CMAKE_ANDROID_ARCH_ABI})
target_link_libraries(${target}
        android
        log
        GLESv3
        ${CMAKE_CURRENT_SOURCE_DIR}/libs/assimp/${CMAKE_ANDROID_ARCH_ABI}/libandroid_jniiosystem.a
        ${CMAKE_CURRENT_SOURCE_DIR}/libs/assimp/${CMAKE_ANDROID_ARCH_ABI}/libassimp.so
        )

