//
// Created by xuzaixiang on 2022/3/29.
//

#ifndef ANIMOJI_JNI_VAO_H
#define ANIMOJI_JNI_VAO_H

#include <GLES3/gl3.h>
#include <vector>

struct JNIVertex {
    float ver_x;
    float ver_y;
    float ver_z;
    float tex_x;
    float tex_y;
};


class JNIVao {
private:
    int index_sze = 0;
public:
    JNIVao(std::vector<JNIVertex> &vertexes, std::vector<unsigned int> &indexes);

    virtual ~JNIVao();

    GLuint buffer{};
    GLuint index{};
    GLuint vao{};

    void draw() const;
};


#endif //ANIMOJI_JNI_VAO_H
