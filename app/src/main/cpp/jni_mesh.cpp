//
// Created by xuzaixiang on 2022/3/29.
//

#include "jni_mesh.h"
#include <vector>
#include <android/log.h>
#include <unordered_map>
#include <jni.h>

std::vector<JNIMesh *> meshes;
std::unordered_map<std::string, JNITexture *> materials_texture;

float max_x = 0;
float max_y = 0;
float max_z = 0;

float jni_max_x(float x) {
    max_x = std::max(abs(x), max_x);
    return x;
}

float jni_max_y(float y) {
    max_y = std::max(abs(y), max_y);
    return y;
}

float jni_max_z(float z) {
    max_z = std::max(abs(z), max_z);
    return z;
}

void get_textures(const aiMaterial *material, aiTextureType type,
                  std::unordered_set<std::string> &materials) {
    for (int i = 0; i < material->GetTextureCount(type); ++i) {
        aiString textPath;
        aiReturn retStatus = material->GetTexture(type, i, &textPath);
        if (retStatus != aiReturn_SUCCESS || textPath.length == 0) {
            __android_log_print(ANDROID_LOG_ERROR, "animoji", "material->GetTexture failed ");
            continue;
        }
        materials.emplace(textPath.C_Str());
    }
}

void get_all_material(const aiScene *scene) {
    std::unordered_set<std::string> materials;
    for (int i = 0; i < scene->mNumMaterials; ++i) {
        aiMaterial *material = scene->mMaterials[i];
        get_textures(material, aiTextureType_SPECULAR, materials);
        get_textures(material, aiTextureType_DIFFUSE, materials);
        get_textures(material, aiTextureType_AMBIENT, materials);
    }
    for (auto &item:materials) {
        auto path = std::string("/data/user/0/com.github.app/files/mask/") + item;
        int x, y, channel;
        auto data = stbi_load(path.c_str(), &x, &y, &channel, 4);
        if (data != nullptr) {
            auto t = new JNITexture();
            glGenTextures(1, &t->id);
            glBindTexture(GL_TEXTURE_2D, t->id);
            glTexStorage2D(GL_TEXTURE_2D, 1, GL_RGBA8, x, y);
            glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, x, y, GL_RGBA, GL_UNSIGNED_BYTE, data);
            assert(glGetError() == GL_NO_ERROR);
            glBindTexture(GL_TEXTURE_2D, GL_NONE);
            stbi_image_free(data);
            materials_texture[item] = t;
        }
    }
    for (auto mesh : meshes) {
        for (auto & vertex : mesh->vertexes) {
            vertex.ver_x /= max_x;
            vertex.ver_y /= max_y;
            vertex.ver_z /= max_z;
        }
    }
}

void get_all_meshes(const aiScene *scene, aiNode *node) {
    for (int i = 0; i < node->mNumMeshes; ++i) {
        aiMesh *mesh = scene->mMeshes[node->mMeshes[i]];
        if (mesh) {
            meshes.emplace_back(new JNIMesh(scene, mesh));
        }
    }
    for (int i = 0; i < node->mNumChildren; ++i) {
        get_all_meshes(scene, node->mChildren[i]);
    }
}


JNIMesh::JNIMesh(const aiScene *scene, aiMesh *mesh) : scene(scene), mesh(mesh) {

    for (int i = 0; i < mesh->mNumVertices; ++i) {
        JNIVertex v{};
        aiVector3D m_vertex = mesh->mVertices[i];
        v.ver_x = jni_max_x(m_vertex.x);
        v.ver_y = jni_max_y(m_vertex.y);
        v.ver_z = jni_max_z(m_vertex.z);
        if (mesh->HasTextureCoords(0)) {
            auto temp = mesh->mTextureCoords[0][i];
            v.tex_x = temp.x;
            v.tex_y = temp.y;
        }
        vertexes.emplace_back(v);
    }

    for (int i = 0; i < mesh->mNumFaces; ++i) {
        aiFace face = mesh->mFaces[i];
        if (face.mNumIndices != 3) {
            __android_log_print(ANDROID_LOG_ERROR, "animoji", " face.mNumIndices != 3 ");
            continue;
        }
        for (int j = 0; j < face.mNumIndices; ++j) {
            indices.push_back(face.mIndices[j]);
        }
    }


    if (mesh->mMaterialIndex >= 0) {
        const aiMaterial *material = scene->mMaterials[mesh->mMaterialIndex];
        get_textures(material, aiTextureType_SPECULAR);
        get_textures(material, aiTextureType_DIFFUSE);
        get_textures(material, aiTextureType_AMBIENT);
    }
//    glEnable(GL_DEPTH_TEST);
//    glCullFace(GL_BACK);
}

void JNIMesh::get_textures(const aiMaterial *material, aiTextureType type) {
    for (int i = 0; i < material->GetTextureCount(type); ++i) {
        aiString textPath;
        aiReturn retStatus = material->GetTexture(type, i, &textPath);
        if (retStatus != aiReturn_SUCCESS || textPath.length == 0) {
            __android_log_print(ANDROID_LOG_ERROR, "animoji", "material->GetTexture failed ");
            continue;
        }
        textures.emplace(textPath.C_Str());
    }
}

void JNIMesh::draw() {
    if (vao == nullptr){
        vao = new JNIVao(vertexes, indices);
    }
    int i = 0;
    for (auto &item:textures) {
        glActiveTexture(GL_TEXTURE0 + i);
        glBindTexture(GL_TEXTURE_2D, materials_texture[item]->id);
        i++;
    }
    vao->draw();
    assert(glGetError() == GL_NO_ERROR);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_github_animoji_PigRender_00024Companion_nativeDraw(JNIEnv *env, jobject thiz) {
//    glFrontFace(GL_CCW);
    for (auto mesh : meshes) {
        mesh->draw();
    }
//    glFrontFace(GL_CCW);
//    meshes[0]->draw();

//    glClearColor(1.0,0.0,0.0,1.0);
//    glClear(GL_COLOR_BUFFER_BIT);
}