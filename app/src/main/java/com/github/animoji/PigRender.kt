package com.github.animoji

import android.content.Context
import android.content.res.AssetManager
import android.opengl.GLES31.*
import com.github.animoji.opengl.GLProgram
import com.github.animoji.opengl.GLShader
import com.github.animoji.opengl.GLVao

class PigRender constructor(val context: Context) {
    companion object {
        external fun load(path: String, assert: AssetManager)
        external fun nativeDraw()

        init {
            System.loadLibrary("animoji_jni")
        }
    }

    init {
        load(context.filesDir.absolutePath, context.assets)
    }

    private val mVert by lazy { GLShader(GL_VERTEX_SHADER, context.readAssert("shader/mask.vert")) }
    private val mFrag by lazy {
        GLShader(
            GL_FRAGMENT_SHADER,
            context.readAssert("shader/mask.frag")
        )
    }
    private val mProgram by lazy { GLProgram(mVert, mFrag) }
//    private val mVertexes = floatArrayOf(
//        -0.5f, -0.5f,1.0f,  // Lower-left
//        0.5f, -0.5f, 1.0f,   // Lower-right
//        -0.5f, 0.5f,1.0f,  // Upper-left
//        0.5f, 0.5f,1.0f    // Upper-right
//    )
//    private val mVao by lazy { GLVao(mVertexes,3) }

    fun draw() {
        mProgram.use()
        val loc = mProgram.getUniformLocation("input_texture")
        glUniform1i(loc, 0)

        nativeDraw()

//        mVao.bind()
//        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4)
//        mVao.unbind()

        assert(glGetError() == GL_NO_ERROR)
    }
}