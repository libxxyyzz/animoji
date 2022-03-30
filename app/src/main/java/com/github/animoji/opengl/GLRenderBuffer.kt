package com.github.animoji.opengl

import android.opengl.GLES20
import android.opengl.GLES30.*


class GLRenderBuffer constructor(val width: Int, val height: Int) {
    private val temp = IntArray(1)

    init {
        glGenRenderbuffers(1, temp, 0)
        glBindRenderbuffer(GL_RENDERBUFFER, temp[0])
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH24_STENCIL8, width, height)
        glBindRenderbuffer(GL_RENDERBUFFER, 0);
    }

    fun bindFrameBuffer() {
        glFramebufferRenderbuffer(
            GL_FRAMEBUFFER,
            GL_DEPTH_STENCIL_ATTACHMENT,
            GL_RENDERBUFFER,
            temp[0]
        )
    }

    fun release() {
        GLES20.glDeleteRenderbuffers(1, temp, 0)
    }
}