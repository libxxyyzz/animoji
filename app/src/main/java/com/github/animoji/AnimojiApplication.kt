package com.github.animoji

import android.app.Application
import android.content.Context
import android.util.Log
import com.alibaba.android.mnnkit.actor.FaceDetector
import com.alibaba.android.mnnkit.intf.InstanceCreatedListener
import java.lang.Error

fun Context.faceDetector(): FaceDetector? {
    val app = if (this is Application) this else this.applicationContext
    return (app as AnimojiApplication).faceDetector
}

class AnimojiApplication : Application() {

    var faceDetector: FaceDetector? = null
    private val mFaceDetectorConfig = FaceDetector.FaceDetectorCreateConfig()

    override fun onCreate() {
        super.onCreate()
        mFaceDetectorConfig.mode = FaceDetector.FaceDetectMode.MOBILE_DETECT_MODE_VIDEO
        FaceDetector.createInstanceAsync(this, mFaceDetectorConfig,
            object : InstanceCreatedListener<FaceDetector> {
                override fun onSucceeded(f: FaceDetector) {
                    faceDetector = f
                }

                override fun onFailed(i: Int, error: Error) {
                    Log.e(TAG, "create face detetector failed: $error")
                }
            })
    }
}