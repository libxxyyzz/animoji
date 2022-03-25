package com.github.animoji.render

import android.content.Context
import android.opengl.GLES11Ext
import android.opengl.GLES31.*
import android.opengl.Matrix
import com.github.animoji.calculateMvp
import com.github.animoji.opengl.GLProgram
import com.github.animoji.opengl.GLShader
import com.github.animoji.opengl.GLVao
import com.github.animoji.readAssert

class GLRender constructor(context: Context) {
    private val mVertex by lazy { GLShader(GL_VERTEX_SHADER, context.readAssert("general.vert")) }
    private val mFragOes by lazy { GLShader(GL_FRAGMENT_SHADER, context.readAssert("oes.frag")) }
    private val mFragRGBA by lazy { GLShader(GL_FRAGMENT_SHADER, context.readAssert("rgba.frag")) }
    private val mProgramOes by lazy { GLProgram(mVertex, mFragOes) }
    private val mProgramRGBA by lazy { GLProgram(mVertex, mFragRGBA) }

    private val mVertexes = floatArrayOf(
        -1.0f, -1.0f,  // Lower-left
        1.0f, -1.0f,    // Lower-right
        -1.0f, 1.0f,  // Upper-left
        1.0f, 1.0f    // Upper-right
    )
    private val mVao by lazy { GLVao(mVertexes) }
    private val mLocalMatrix = FloatArray(16)

    fun release() {
        mVertex.release()
        mFragOes.release()
        mFragRGBA.release()
        mProgramOes.release()
        mProgramRGBA.release()
        mVao.release()
    }

    fun drawOes(id: Int, r: Int, dr: Int, m: FloatArray, w: Int, h: Int, sw: Int, sh: Int) {
        glActiveTexture(GL_TEXTURE0)
        glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, id)
        draw(mProgramOes, r, dr, m, w, h, sw, sh)
    }

    fun drawRGBA(id: Int, r: Int, dr: Int, m: FloatArray, w: Int, h: Int, sw: Int, sh: Int) {
        glActiveTexture(GL_TEXTURE0)
        glBindTexture(GL_TEXTURE_2D, id)
        draw(mProgramRGBA, r, dr, m, w, h, sw, sh)
    }

    private fun draw(
        p: GLProgram,
        r: Int,
        dr: Int,
        m: FloatArray,
        w: Int,
        h: Int,
        sw: Int,
        sh: Int
    ) {
        p.use()
        val mLocTexMatrix = p.getUniformLocation("texTransform")
        val mLocMvpMatrix = p.getUniformLocation("mvpTransform")
        val loc = p.getUniformLocation("input_texture")
        glUniform1i(loc, 0)
        glViewport(0, 0, sw, sh)
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        glClear(GL_COLOR_BUFFER_BIT)
        System.arraycopy(m, 0, mLocalMatrix, 0, 16)
        when (dr) {
            90 -> Matrix.translateM(mLocalMatrix, 0, 0f, 1f, 0f)
            180 -> Matrix.translateM(mLocalMatrix, 0, 1f, 1f, 0f)
            270 -> Matrix.translateM(mLocalMatrix, 0, 1f, 0f, 0f)
        }
        Matrix.rotateM(mLocalMatrix, 0, -dr + 0f, 0f, 0f, 1f)
        glUniformMatrix4fv(mLocTexMatrix, 1, false, mLocalMatrix, 0)

        calculateMvp(r, w, h, sw, sh, mLocalMatrix)
        glUniformMatrix4fv(mLocMvpMatrix, 1, false, mLocalMatrix, 0)
        mVao.bind()
        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4)
        mVao.unbind()
    }
}