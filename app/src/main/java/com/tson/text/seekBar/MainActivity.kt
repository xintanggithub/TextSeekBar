package com.tson.text.seekBar

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.tson.text.seekbar.event.Down
import com.tson.text.seekbar.event.Event
import com.tson.text.seekbar.event.Up
import com.tson.text.seekbar.listener.SeekBarViewOnChangeListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var percent = 0.0f

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        shapeBgMB.percent(0.5f)
        multiSeekBarMSB.percent(0.5f)
        btnTest.setOnClickListener {
            percent = 0f
            looperAdd()
        }
        shapeBgMB.addOnChangeListener(object : SeekBarViewOnChangeListener {
            override fun touch(percent: Float, eventType: Event) {
                when(eventType) {
                    Down -> Log.e("touch", "Down")
                    Up -> Log.e("touch", "Up")
                    else -> Log.e("touch", "Move   percent = $percent     |  eventType = $eventType")
                }
            }
        })
    }

    private fun looperAdd() {
        if (percent in 0f..1f) {
            percent += 0.005f
            runOnUiThread {
                shapeBgMB.percent(percent)
                multiSeekBarMSB.percent(percent)
            }
            Handler().postDelayed({
                looperAdd()
            }, 15)
        }
    }

}