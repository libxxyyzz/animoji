package com.github.animoji

import android.content.Context
import android.content.Intent
import android.opengl.GLES30
import android.opengl.GLES31
import android.opengl.Matrix
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.github.animoji.base.FaceDetectorActivity
import com.github.animoji.manager.RotationManager
import com.github.animoji.opengl.GLFramebuffer
import com.github.animoji.opengl.GLProgram
import com.github.animoji.opengl.GLShader
import com.github.animoji.opengl.GLVao
import com.github.animoji.render.GLRender
import kotlin.concurrent.fixedRateTimer

class TestLandmarkActivity : FaceDetectorActivity() {

    companion object {
        fun launch(context: Context) {
            context.startActivity(Intent(context, TestLandmarkActivity::class.java))
        }
    }

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
            GLES31.GL_VERTEX_SHADER,
            readAssert("shader/test_landmark.vert")
        )
    }
    private val mLandmarkFrag by lazy {
        GLShader(
            GLES31.GL_FRAGMENT_SHADER,
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
        setContentView(R.layout.activity_test_landmark)
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

    override fun onUpdate(oes: Int) {
        if (mFramebuffer == null)
            mFramebuffer = GLFramebuffer(cameraWidth, cameraHeight)
        mFramebuffer?.bind()
        mRender.drawOes(
            oes,
            0,
            0,
            mLocalMatrix,
            cameraWidth,
            cameraHeight,
            cameraWidth,
            cameraHeight
        )

        mLandmarkVao.update(landmarks)
        mLandmarkProgram.use()
        mLandmarkVao.bind()
        GLES31.glDrawArrays(GLES30.GL_POINTS, 0, 106)
        mLandmarkVao.unbind()


        test(cameraWidth, cameraHeight)
        mFramebuffer?.unbind()

        mRender.drawRGBA(
            mFramebuffer!!.texture, cameraRotation, mRotationManager.displayRotation,
            cameraMatrix, cameraWidth, cameraHeight, mSurfaceWidth, mSurfaceHeight
        )
    }
}