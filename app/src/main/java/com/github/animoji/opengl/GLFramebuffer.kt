package com.github.animoji.opengl

import android.opengl.GLES20
import android.opengl.GLES30.*

class GLFramebuffer constructor(width: Int, height: Int) {
    private var mFramebuffer = 0
    var texture = 0

    init {
        val temp = IntArray(1)
        GLES20.glGenFramebuffers(1, temp, 0).apply { mFramebuffer = temp[0] }
        GLES20.glGenTextures(1, temp, 0).apply { texture = temp[0] }
        glBindTexture(GL_TEXTURE_2D, texture)
        glTexStorage2D(GL_TEXTURE_2D, 1, GL_RGBA8, width, height)
        glBindFramebuffer(GL_FRAMEBUFFER, mFramebuffer)
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, texture, 0)
    }

    fun bind() {
        glBindFramebuffer(GL_FRAMEBUFFER, mFramebuffer)
    }

    fun unbind() {
        glBindFramebuffer(GL_FRAMEBUFFER, GL_NONE)
    }

    fun release() {
        GLES20.glDeleteTextures(1, intArrayOf(texture), 0)
        GLES20.glDeleteFramebuffers(1, intArrayOf(mFramebuffer), 0)
    }
}