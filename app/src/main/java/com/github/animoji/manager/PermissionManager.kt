package com.github.animoji.manager

import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class PermissionManager constructor(
    private val activity: AppCompatActivity,
    private val permission:String,
    private val callback: (Boolean) -> Unit
) {
    private val request =
        activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            callback(it)
        }

    fun request() {
        request.launch(permission)
    }
}