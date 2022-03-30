package com.github.animoji.base

import android.graphics.Rect
import android.util.Log
import androidx.camera.core.ImageProxy
import com.alibaba.android.mnnkit.entity.FaceDetectConfig
import com.alibaba.android.mnnkit.entity.MNNCVImageFormat
import com.alibaba.android.mnnkit.entity.MNNFlipType
import com.github.animoji.TAG
import com.github.animoji.faceDetector

open class FaceDetectorActivity : CameraActivity() {

    protected val landmarks = FloatArray(212)
    protected val faceRect = Rect()
    protected var hadFace = false
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
//            cameraRotation,// 原图方向关键点
            0,// 需要正向的人脸关键点
            if (isFront()) MNNFlipType.FLIP_Y else MNNFlipType.FLIP_NONE
        )

        var rWidth = width
        var rHeight = height
        if (cameraRotation == 90 || cameraRotation == 270) {
            rWidth = height
            rHeight = width
        }
        Log.e(TAG, "onAnalysis: -- ${report?.size ?: 0}")

        if (!report.isNullOrEmpty()) {
            hadFace = true
            val face = report[0]
            faceRect.set(face.rect)
            System.arraycopy(face.keyPoints, 0, landmarks, 0, 212)
            yaw = face.yaw
            pitch = face.pitch
            roll = face.roll
            for (i in 0 until 106) {
                landmarks[i * 2] /= rWidth + 0f
                landmarks[i * 2 + 1] /= rHeight + 0f
            }
        } else {
            for (i in 0 until 212) landmarks[i] = 0f
            hadFace = false
        }
        proxy.close()
    }
}