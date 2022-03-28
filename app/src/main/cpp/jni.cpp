#include <jni.h>

//
// Created by xuzaixiang on 2022/3/28.
//
//#include <assimp/Importer.hpp>
//#include <assimp/scene.h>
//#include <assimp/postprocess.h>
//#include <AndroidJNIIOSystem.h>
#include <android/log.h>
#include <string>

#define TINYGLTF_IMPLEMENTATION
#define STB_IMAGE_IMPLEMENTATION
#define STB_IMAGE_WRITE_IMPLEMENTATION

#include "tinygltf/tiny_gltf.h"

const char *tag = "animoji_jni";

using namespace tinygltf;

Model model;

extern "C"
JNIEXPORT void JNICALL
Java_com_github_animoji_PigRender_00024Companion_load(JNIEnv *env, jobject thiz, jstring path,
                                                      jobject assert) {
    TinyGLTF loader;
    std::string err;
    std::string warn;
    auto path_ = env->GetStringUTFChars(path, nullptr);
    std::string gltf_path = path_;
    gltf_path += "/mask/scene.gltf";
    bool ret = loader.LoadASCIIFromFile(&model, &err, &warn, gltf_path);
    __android_log_print(ANDROID_LOG_ERROR, tag, "load gltf %s",
                        ret ? "success" : (std::string("fail : ") + err).c_str());
}

