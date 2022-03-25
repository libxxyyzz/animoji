package com.github.animoji.manager

import android.app.Activity
import android.content.Context
import android.hardware.display.DisplayManager
import android.os.Handler
import android.view.Surface

class RotationManager constructor(
    private val activity: Activity
) {

    var displayRotation = 0

    private val displayManager by lazy {
        activity.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
    }

    private fun calRotation() {
        activity.window.apply {
            decorView.post {
                displayRotation = when (decorView.display.rotation) {
                    Surface.ROTATION_0 -> 0
                    Surface.ROTATION_90 -> 90
                    Surface.ROTATION_180 -> 180
                    Surface.ROTATION_270 -> 270
                    else -> 0
                }
            }
        }
    }

    private val displayListener = object : DisplayManager.DisplayListener {
        override fun onDisplayAdded(displayId: Int) = Unit
        override fun onDisplayRemoved(displayId: Int) = Unit
        override fun onDisplayChanged(displayId: Int): Unit {
            calRotation()
        }
    }

    fun onCreate() {
        calRotation()
        displayManager.unregisterDisplayListener(displayListener)
    }

    fun onDestroy() {
        displayManager.registerDisplayListener(displayListener, Handler(activity.mainLooper))
    }
}