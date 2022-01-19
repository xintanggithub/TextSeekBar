package com.tson.text.seekBar

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tson.text.seekbar.event.Event
import com.tson.text.seekbar.listener.SeekBarViewOnChangeListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        tsb3.addOnChangeListener(object : SeekBarViewOnChangeListener {
//            override fun touch(percent: Float, eventType: Event) {
//                tsb3Tv.text = "普通进度条：${(percent * 100).toInt()}%"
//            }
//        })
    }
}