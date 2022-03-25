package com.github.animoji.base

import android.util.Log
import androidx.camera.core.ImageProxy
import com.alibaba.android.mnnkit.entity.FaceDetectConfig
import com.alibaba.android.mnnkit.entity.MNNCVImageFormat
import com.alibaba.android.mnnkit.entity.MNNFlipType
import com.github.animoji.TAG
import com.github.animoji.faceDetector

open class FaceDetectorActivity : CameraActivity() {

    protected val landmarks = FloatArray(212)
    protected var roll = 0f // 旋转
    protected var pitch = 0f // 点头
    protected var yaw = 0f//  摇头

    private var mBytes: ByteArray? = null

    override fun onAnalysis(proxy: ImageProxy) {
        val width = proxy.width
        val height = proxy.height
        if (mBytes == null || mBytes?.size != width * height) {
            mBytes = ByteArray(width * height)
        }
        proxy.planes[0].buffer.get(mBytes!!)
        val detectConfig =
            FaceDetectConfig.ACTIONTYPE_HEAD_YAW or FaceDetectConfig.ACTIONTYPE_HEAD_PITCH
        val report = faceDetector()?.inference(
            mBytes,
            width,
            height,
            MNNCVImageFormat.GRAY,
            detectConfig,
            cameraRotation,
            cameraRotation,// 原图方向关键点
//            0,// 需要正向的人脸关键点
            MNNFlipType.FLIP_NONE
        )

        Log.e(TAG, "onAnalysis: -- ${report?.size ?: 0}")

        if (!report.isNullOrEmpty()) {
            val face = report[0]
            System.arraycopy(face.keyPoints, 0, landmarks, 0, 212)
            yaw = face.yaw
            pitch = face.pitch
            roll = face.roll
            for (i in 0 until 106) {
                landmarks[i * 2] /= width + 0f
                landmarks[i * 2 + 1] /= height + 0f
            }
        } else {
            for (i in 0 until 212) landmarks[i] = 0f
        }
        proxy.close()
    }
}