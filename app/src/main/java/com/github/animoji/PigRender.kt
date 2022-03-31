package com.github.animoji

import android.content.Context
import android.content.res.AssetManager
import android.opengl.GLES20
import android.opengl.GLES31.*
import android.opengl.Matrix
import com.github.animoji.opengl.GLProgram
import com.github.animoji.opengl.GLShader

class PigRender constructor(val context: Context) {
    companion object {
        external fun load(path: String, assert: AssetManager)
        external fun nativeDraw()
        external fun nativeGetModelAdjust(): FloatArray
        external fun nativeGetMaxViewDistance(): Float
        external fun nativeGetMaxXY(): FloatArray
        external fun nativeRelease()

        init {
            System.loadLibrary("animoji_jni")
        }
    }


    private val mModel = FloatArray(16)
    private val mView = FloatArray(16)
    private val mProjection = FloatArray(16)
    private val temp = FloatArray(32)
    private val mMvpMatrix = FloatArray(16)

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
    private val mModelAdjust by lazy { nativeGetModelAdjust() }
    private val mMaxViewDistance by lazy { nativeGetMaxViewDistance() }
    private val mMaxXYZ by lazy { nativeGetMaxXY() }

    fun release() {
        mProgram.release()
        nativeRelease()
    }

//    private val mVertexes = floatArrayOf(
//        -0.5f, -0.5f,1.0f,  // Lower-left
//        0.5f, -0.5f, 1.0f,   // Lower-right
//        -0.5f, 0.5f,1.0f,  // Upper-left
//        0.5f, 0.5f,1.0f    // Upper-right
//    )
//    private val mVao by lazy { GLVao(mVertexes,3) }

    var degreeY = 90f
    var degreeX = 0f

    fun draw(
        ratio: Float,
        yaw: Float,
        pitch: Float,
        roll: Float,
        centerX: Float,
        centerY: Float,
        scale: Float
    ) {
//        Log.e(TAG, "draw: $centerX ------- $centerY")
        mProgram.use()
        val loc = mProgram.getUniformLocation("input_texture")
        glUniform1i(loc, 0)

        Matrix.setIdentityM(mModel, 0)
        Matrix.setIdentityM(mView, 0)
        Matrix.setIdentityM(mProjection, 0)

        //model
//        Matrix.translateM(
//            mModel, 0,
//            (centerX * 2 - 1f) * mMaxXYZ[0],
////            0f,
//            0f,
//            -mMaxXYZ[1]/2 // (y) 原始角度 z 轴 90度
////            ((1.0f - centerY) * 2 - 1)*mMaxXY[1], 0f
//        )

        Matrix.scaleM(mModel, 0, scale * 1.8f, scale * 1.8f, 1f)
        Matrix.rotateM(mModel, 0, degreeY - pitch * 60f, 1f, 0f, 0f)
        Matrix.rotateM(mModel, 0, -roll * 60f, 0f, 1f, 0f)
        Matrix.rotateM(mModel, 0, yaw * 60f, 0f, 0f, 1f)

//        Matrix.translateM(mModel, 0, -mModelAdjust[0], -mModelAdjust[1], 0f)
//        Matrix.translateM(mModel, 0, centerX * 2 - 1, (1.0f - centerY) * 2 - 1, 0f)
//        Matrix.translateM(mModel, 0, -mModelAdjust[0], -mModelAdjust[1], -mModelAdjust[2])

        // view
        Matrix.setLookAtM(mView, 0, 0f, 0f, mMaxViewDistance * 1.8f, 0f, 0f, 0f, 0f, 1f, 0f)
        // projection
        Matrix.frustumM(mProjection, 0, -ratio, ratio, -1.0f, 1.0f, 1.0f, mMaxViewDistance * 4.0f)

        Matrix.multiplyMM(temp, 0, mView, 0, mModel, 0);
        Matrix.multiplyMM(mMvpMatrix, 0, mProjection, 0, temp, 0)

        val locMvp = mProgram.getUniformLocation("mvpMatrix")
        GLES20.glUniformMatrix4fv(locMvp, 1, false, mMvpMatrix, 0)

        nativeDraw()

//        mVao.bind()
//        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4)
//        mVao.unbind()

        assert(glGetError() == GL_NO_ERROR)
    }
}