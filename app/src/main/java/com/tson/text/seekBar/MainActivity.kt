package com.tson.text.seekBar

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tson.text.seekbar.event.Event
import com.tson.text.seekbar.listener.SeekBarViewOnChangeListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tsb3.addOnChangeListener(object : SeekBarViewOnChangeListener {
            override fun touch(percent: Float, eventType: Event) {
                tsb3.changePercent(percent, "${(percent * 100).toInt()}%")
            }
        })
    }
}