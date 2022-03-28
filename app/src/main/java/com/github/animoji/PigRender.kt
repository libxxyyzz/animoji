package com.github.animoji

import android.content.Context
import android.content.res.AssetManager

class PigRender constructor(val context: Context) {
    companion object {
        external fun load(path: String, assert: AssetManager)

        init {
            System.loadLibrary("animoji_jni")
        }
    }

    init {
        load(context.filesDir.absolutePath, context.assets)
    }

    fun draw() {

    }
}