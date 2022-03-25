package com.github.animoji

import android.graphics.Bitmap
import android.opengl.GLES20
import android.opengl.GLES30
import android.opengl.Matrix
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.github.animoji.base.FaceDetectorActivity
import com.github.animoji.manager.RotationManager
import com.github.animoji.opengl.GLFramebuffer
import com.github.animoji.opengl.GLShader
import com.github.animoji.opengl.GLVao
import com.github.animoji.render.GLRender
import java.nio.ByteBuffer
import android.opengl.GLES31.*
import com.github.animoji.opengl.GLProgram

class MainActivity : FaceDetectorActivity() {

    private val mSurfaceView: SurfaceView by lazy { findViewById(R.id.surface_view) }
    private var mSurfaceWidth = 0
    private var mSurfaceHeight = 0
    private val mRender by lazy { GLRender(this) }
    private val mRotationManager by lazy { RotationManager(this) }
    private val mLocalMatrix = FloatArray(16)

    private var mFramebuffer: GLFramebuffer? = null

    private val mLandmarkVao by lazy { GLVao(landmarks) }
    private val mLandmarkVert by lazy {
        GLShader(
            GL_VERTEX_SHADER,
            readAssert("shader/landmark.vert")
        )
    }
    private val mLandmarkFrag by lazy {
        GLShader(
            GL_FRAGMENT_SHADER,
            readAssert("shader/landmark.frag")
        )
    }
    private val mLandmarkProgram by lazy { GLProgram(mLandmarkVert, mLandmarkFrag) }

    override fun onDestroy() {
        super.onDestroy()
        mRotationManager.onDestroy()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mRotationManager.onCreate()
        Matrix.setIdentityM(mLocalMatrix, 0)
        mSurfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                registerSurface(holder.surface)
                makeCurrent(holder.surface)
            }

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {
                mSurfaceWidth = width
                mSurfaceHeight = height
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                unRegisterSurface(holder.surface)
            }
        })
    }


    private fun test(width: Int, height: Int) {
        val buffer = ByteBuffer.allocateDirect(width * height * 4);
        buffer.position(0)
        GLES30.glReadPixels(
            0,
            0,
            width,
            height,
            GLES30.GL_RGBA,
            GLES30.GL_UNSIGNED_BYTE,
            buffer
        )
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val bWidth = bitmap.width
        val bHeight = bitmap.height
        bitmap.copyPixelsFromBuffer(buffer)
        Log.e(TAG, "test: -------- check bitmap")
    }

    override fun onUpdate(oes: Int) {
        // test origin image
//        if (mFramebuffer == null)
//            mFramebuffer = GLFramebuffer(cameraWidth, cameraHeight)
//        mFramebuffer?.bind()
//        mRender.drawOes(
//            oes,
//            0,
//            0,
//            mLocalMatrix, cameraWidth,
//            cameraHeight,
//            cameraWidth,
//            cameraHeight
//        )
//        test(cameraWidth, cameraHeight)

        // show oes directly
//        mRender.drawOes(
//            oes,
//            cameraRotation,
//            mRotationManager.displayRotation,
//            cameraMatrix,
//            cameraWidth,
//            cameraHeight,
//            mSurfaceWidth,
//            mSurfaceHeight
//        )


        var texWidth = cameraWidth
        var texHeight = cameraHeight
        if (cameraRotation == 90 || cameraRotation == 270) {
            texWidth = cameraHeight
            texHeight = cameraWidth
        }
        if (mFramebuffer == null)
            mFramebuffer = GLFramebuffer(texWidth, texHeight)
        mFramebuffer?.bind()

        // draw oes
        mRender.drawOes(
            oes,
            cameraRotation,
            0,
            cameraMatrix, cameraWidth,
            cameraHeight,
            texWidth,
            texHeight
        )

        // draw landmarks
        mLandmarkVao.update(landmarks)
        mLandmarkProgram.use()
        mLandmarkVao.bind()
        val locMvp = mLandmarkProgram.getUniformLocation("texTransform")
        glUniformMatrix4fv(locMvp, 1, false, cameraMatrix, 0)
        glDrawArrays(GLES30.GL_POINTS, 0, 106)
        mLandmarkVao.unbind()


//        test(texWidth, texHeight)

        mFramebuffer?.unbind()

        mRender.drawRGBA(
            mFramebuffer!!.texture, 0,
            mRotationManager.displayRotation,
            mLocalMatrix,
            texWidth,
            texHeight,
            mSurfaceWidth,
            mSurfaceHeight
        )
    }


}