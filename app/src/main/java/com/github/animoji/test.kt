package com.github.animoji

import android.graphics.Bitmap
import android.opengl.GLES30
import android.util.Log
import java.nio.ByteBuffer

fun test(width: Int, height: Int) {
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