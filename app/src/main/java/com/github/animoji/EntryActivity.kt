package com.github.animoji

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class EntryActivity : AppCompatActivity() {

    private val tvDae: TextView by lazy { findViewById(R.id.tv_dae) }
//    private val tvLandmark: TextView by lazy { findViewById(R.id.tv_landmark) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entry)
        tvDae.setOnClickListener { MainActivity.launch(this) }
//        tvLandmark.setOnClickListener { TestLandmarkActivity.launch(this) }
    }
}