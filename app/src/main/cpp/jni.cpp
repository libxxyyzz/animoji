#include <jni.h>

//
// Created by xuzaixiang on 2022/3/28.
//
#include <assimp/Importer.hpp>
#include <assimp/scene.h>
#include <assimp/postprocess.h>
#include <assimp/DefaultIOStream.h>
#include <AndroidJNIIOSystem.h>
#include <android/log.h>
#include <string>
#include "jni_mesh.h"

//#define TINYGLTF_IMPLEMENTATION
//#define STB_IMAGE_IMPLEMENTATION
//#define STB_IMAGE_WRITE_IMPLEMENTATION
//#include "tinygltf/stb_image.h"
//#include "tinygltf/tiny_gltf.h"
//using namespace tinygltf;
//Model model;

const char *tag = "animoji_jni";

extern "C"
JNIEXPORT void JNICALL
Java_com_github_animoji_PigRender_00024Companion_load(JNIEnv *env, jobject thiz, jstring path,
                                                      jobject assert) {

//    TinyGLTF loader;
//    std::string err;
//    std::string warn;
//    auto path_ = env->GetStringUTFChars(path, nullptr);
//    std::string gltf_path = path_;
//    gltf_path += "/mask/scene.gltf";
//    bool ret = loader.LoadASCIIFromFile(&model, &err, &warn, gltf_path);
//    __android_log_print(ANDROID_LOG_ERROR, tag, "load gltf %s",
//                        ret ? "success" : (std::string("fail : ") + err).c_str());

    auto importer = new Assimp::Importer();
    const char *path_char = env->GetStringUTFChars(path, 0);
    auto manager = AAssetManager_fromJava(env, assert);
    auto *ioSystem = new Assimp::AndroidJNIIOSystem(path_char, manager);
    importer->SetIOHandler(ioSystem);
    auto scene = importer->ReadFile("mask/scene.gltf",
                                    aiProcess_Triangulate | aiProcess_FlipUVs |
                                    aiProcess_CalcTangentSpace);
    if (scene == nullptr) {
        __android_log_print(ANDROID_LOG_ERROR, tag, "importer->ReadFile : %s",
                            importer->GetErrorString());
    }
    env->ReleaseStringUTFChars(path, path_char);

    get_all_meshes(scene, scene->mRootNode);
    get_all_material(scene);

//    stbi_load("")
    delete importer;
}

