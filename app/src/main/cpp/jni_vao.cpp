//
// Created by xuzaixiang on 2022/3/29.
//

#include "jni_vao.h"

JNIVao::~JNIVao() {
    glDeleteBuffers(1, &buffer);
    glDeleteVertexArrays(1, &vao);
}

JNIVertex test_vertex[4]{
        {-0.5, -0.5, 1.0, 0.0, 0.0},
        {0.5,  -0.5, 1.0, 1.0, 0.0},
        {-0.5, 0.5,  1.0, 0.0, 1.0},
        {1.0,  1.0,  1.0, 1.0, 1.0}
};

JNIVao::JNIVao(std::vector<JNIVertex> &vertexes, std::vector<unsigned int> &indexes) : index_sze(
        indexes.size()) {
    glGenBuffers(1, &buffer);
    glBindBuffer(GL_ARRAY_BUFFER, buffer);
    glBufferData(GL_ARRAY_BUFFER, vertexes.size() * sizeof(JNIVertex), vertexes.data(),
                 GL_STATIC_DRAW);
//    glBufferData(GL_ARRAY_BUFFER, 4 * sizeof(JNIVertex), test_vertex,  GL_STATIC_DRAW);

    glGenBuffers(1, &index);
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, index);
    glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexes.size() * sizeof(unsigned int), indexes.data(),
                 GL_STATIC_DRAW);
//    unsigned int test_index[6] = {0,1,2,1,3,2};
//    glBufferData(GL_ELEMENT_ARRAY_BUFFER, 6 * sizeof(unsigned int), test_index, GL_STATIC_DRAW);
//    index_sze = 6;

    glGenVertexArrays(1, &vao);
    glBindVertexArray(vao);
    glBindBuffer(GL_ARRAY_BUFFER, buffer);
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, index);
    glEnableVertexAttribArray(0);
    glVertexAttribPointer(0, 3, GL_FLOAT, GL_FALSE, 5 * sizeof(GLfloat), 0);
    glEnableVertexAttribArray(1);
    glVertexAttribPointer(1, 2, GL_FLOAT, GL_FALSE, 5 * sizeof(GLfloat),
                          reinterpret_cast<const void *>(3 * sizeof(GLfloat)));
    glBindVertexArray(GL_NONE);
    assert(glGetError() == GL_NO_ERROR);
}

void JNIVao::draw() const {
    glBindVertexArray(vao);
    glDrawElements(GL_TRIANGLES, index_sze, GL_UNSIGNED_INT, 0);
    glBindVertexArray(GL_NONE);
}

void JNIVao::update(void *data,size_t size) {
    glBindBuffer(GL_ARRAY_BUFFER, buffer);
    glBufferSubData(GL_ARRAY_BUFFER, 0, size, data);
    glBindBuffer(GL_ARRAY_BUFFER, GL_NONE);
}
