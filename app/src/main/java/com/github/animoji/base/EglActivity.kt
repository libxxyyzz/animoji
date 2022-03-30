package com.github.animoji.base

import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.view.Surface
import androidx.appcompat.app.AppCompatActivity
import com.github.animoji.R
import com.github.animoji.egl.EglCore
import com.github.animoji.egl.EglSurfaceBase
import com.github.animoji.egl.OffscreenSurface
import com.github.animoji.egl.WindowSurface
import java.util.concurrent.Executor
import java.util.concurrent.FutureTask

private fun Handler.sync(runnable: Runnable) {
    val future = FutureTask { runnable.run() }
    post(future)
    future.get()
}

open class EglActivity : AppCompatActivity() {

    protected val eglExecutor by lazy { Executor { eglHandler.post(it) } }
    protected val eglHandler by lazy { Handler(mEglThread.looper) }

    private val mEglThread = HandlerThread("CameraThread").apply { start() }
    private var mEgl: EglCore? = null
    private var mEglSurface: OffscreenSurface? = null
    private var mRegisterSurface = mutableMapOf<Surface, WindowSurface>()
    private var mCurrentSurface: EglSurfaceBase? = mEglSurface

    override fun onDestroy() {
        super.onDestroy()
        eglHandler.sync {
            mEglSurface?.release()
            mEgl?.release()
        }
        mEglThread.quitSafely()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        eglHandler.sync {
            mEgl = EglCore(null, EglCore.FLAG_TRY_GLES3)
            mEglSurface = OffscreenSurface(mEgl, 1, 1)
            mEglSurface?.makeCurrent()
        }
    }

    private fun resetCurrentSurface() {
        mEglSurface?.makeCurrent()
        mCurrentSurface = mEglSurface
    }

    fun registerSurface(surface: Surface) {
        eglHandler.sync {
            if (mRegisterSurface.containsKey(surface))
                throw RuntimeException("you had register the surface !")
            mRegisterSurface[surface] = WindowSurface(mEgl, surface, false)
        }
    }

    fun unRegisterSurface(surface: Surface) {
        eglHandler.sync {
            if (!mRegisterSurface.containsKey(surface))
                throw RuntimeException("you had not register the surface !")
            mRegisterSurface[surface]?.apply {
                if (this.isCurrent) resetCurrentSurface()
                release()
            }
        }
    }

    fun makeCurrent(surface: Surface) {
        eglHandler.sync {
            if (!mRegisterSurface.containsKey(surface))
                throw RuntimeException("you had not register the surface !")
            mRegisterSurface[surface]?.apply {
                makeCurrent()
                mCurrentSurface = this
            }
        }
    }

    fun swapBuffer() {
        mCurrentSurface?.swapBuffers()
    }

    fun postInEgl(runnable: Runnable) {
        eglHandler.sync(runnable)
    }
}