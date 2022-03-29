package com.github.animoji

import android.app.Application
import android.content.Context
import android.util.Log
import com.alibaba.android.mnnkit.actor.FaceDetector
import com.alibaba.android.mnnkit.intf.InstanceCreatedListener
import java.io.File
import java.io.FileOutputStream
import java.lang.Error
import kotlin.concurrent.thread

fun Context.faceDetector(): FaceDetector? {
    val app = if (this is Application) this else this.applicationContext
    return (app as AnimojiApplication).faceDetector
}

fun Context.copyFileFromAsserts(inp: String, oup: String) {
    val inpStream = assets.open(inp)
    val oupStream = FileOutputStream(oup)
    val buffer = ByteArray(1024)
    var len = 0
    while (true) {
        len = inpStream.read(buffer)
        if (len > 0) {
            oupStream.write(buffer, 0, len)
        } else {
            break
        }
    }
    inpStream.close()
    oupStream.flush()
    oupStream.close()
}

fun Context.copyDirFromAsserts(dir: String) {
    val files = assets.list(dir) ?: return
    val destDir = File(filesDir.absolutePath + File.separator + dir)
    if (!destDir.exists()) destDir.mkdirs()
    for (f in files) {
        val inp = dir + File.separator + f
        val oup = destDir.absolutePath + File.separator + f
        if (f.contains(".")) {
            copyFileFromAsserts(inp, oup)
        } else {
            copyDirFromAsserts(inp)
        }
    }
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

        thread {
            copyDirFromAsserts("mask")
        }
    }
}