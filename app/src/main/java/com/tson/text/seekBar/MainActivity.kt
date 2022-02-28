package com.tson.text.seekBar

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var percent = 0.0f

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnTest.setOnClickListener {
            percent = 0f
            looperAdd()
        }
    }

    private fun looperAdd() {
        if (percent in 0f..1f) {
            percent += 0.005f
            runOnUiThread { multiSeekBarMSB.percent(percent) }
            Handler().postDelayed({
                looperAdd()
            },15)
        }
    }

}