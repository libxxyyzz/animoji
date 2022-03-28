package com.github.animoji

import android.content.Context
import android.content.Intent
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
import android.widget.TextView
import com.github.animoji.opengl.GLProgram

class MainActivity : FaceDetectorActivity() {

    companion object {
        fun launch(context: Context) {
            context.startActivity(Intent(context, MainActivity::class.java))
        }
    }

    private val mSurfaceView: SurfaceView by lazy { findViewById(R.id.surface_view) }
    private val mTvToggle: TextView by lazy { findViewById(R.id.tv_toggle) }
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

    private val pigRender by lazy { PigRender(this) }

    override fun onDestroy() {
        super.onDestroy()
        mRotationManager.onDestroy()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mRotationManager.onCreate()
        Matrix.setIdentityM(mLocalMatrix, 0)
        mTvToggle.setOnClickListener {
            toggle()
        }
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
        glDrawArrays(GLES30.GL_POINTS, 0, 106)
        mLandmarkVao.unbind()

        // draw pig
        pigRender.draw()

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