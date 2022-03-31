//
// Created by xuzaixiang on 2022/3/29.
//

#ifndef ANIMOJI_JNI_MESH_H
#define ANIMOJI_JNI_MESH_H

#include <assimp/Importer.hpp>
#include <assimp/scene.h>
#include <assimp/postprocess.h>
#include <assimp/DefaultIOStream.h>

#include <GLES3/gl3.h>

#include <unordered_set>
#include <vector>

#define STBI_ONLY_PNG
#define STB_IMAGE_STATIC
#define STB_IMAGE_IMPLEMENTATION

#include "stb_image.h"

#include "jni_vao.h"

void get_all_meshes(const aiScene *scene, aiNode *node);

void get_all_material(const aiScene *scene);


struct JNITexture {
    GLuint id;

    ~JNITexture() {
        glDeleteTextures(1, &id);
    };
};

class JNIMesh {
    const aiScene *scene;
    aiMesh *mesh;
public:
    JNIMesh(const aiScene *scene, aiMesh *mesh);

    std::unordered_set<std::string> textures;
    JNIVao *vao = nullptr;

    void get_textures(const aiMaterial *material, aiTextureType type);

    void draw();

    virtual ~JNIMesh() {
        delete vao;
        vao = nullptr;
    };

    std::vector<JNIVertex> vertexes;
    std::vector<std::vector<JNIVertex>> mAniVertex;
    std::vector<float> mWeight;
    std::vector<unsigned int> indices;
};


#endif //ANIMOJI_JNI_MESH_H
