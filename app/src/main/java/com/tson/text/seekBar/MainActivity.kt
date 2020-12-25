package com.tson.text.seekBar

import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.tson.text.seekbar.SeekBarViewOnChangeListener
import com.tson.text.seekbar.SeekBarViewOnChangeListener.Companion.MOVE
import com.tson.text.seekbar.SeekBarViewOnChangeListener.Companion.UP
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        private const val SECOND = 1000L
        private const val MINE = 60
    }

    private val maxTime = 130//秒

    private var currentTime = 0

    private var isRun = false
    private var isSleep = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        notifyProgress()
        tsb3.addOnChangeListener(object : SeekBarViewOnChangeListener {
            override fun touch(percent: Float, eventType: Int) {
                when (eventType) {
                    UP -> {
                        isSleep = true
                        tsb3.isEnable = true
                        currentTime = (maxTime * percent).toInt()
                        tsb3.setPercent(tsb3.getPercent(), "正在缓冲...")
                        Handler().postDelayed({
                            tsb3.isEnable = false
                            isSleep = false
                            notifyProgress()
                        }, 1500)
                    }
                    MOVE -> {
                        currentTime = (maxTime * percent).toInt()
                        notifyProgress()
                    }
                }
            }
        })
        play.setOnClickListener {
            if (isRun) {
                return@setOnClickListener
            }
            isRun = true
            looperCount()
        }
    }

    private fun notifyProgress() {
        tsb3.setPercent(tsb3.getPercent(), getFormatTime())
    }

    private fun looperCount() {
        Handler().postDelayed({
            if (isSleep) {
                looperCount()
            } else {
                currentTime++
                if (currentTime >= maxTime) {
                    isRun = false
                    return@postDelayed
                } else {
                    val current = currentTime.toFloat() / maxTime.toFloat()
                    Log.w("TextSeekBar", "current =$current")
                    tsb3.setPercent(current, getFormatTime())
                    looperCount()
                }
            }
        }, SECOND)
    }

    private fun getFormatTime(): String {
        val m = currentTime / MINE
        val mStr = if (m < 10) "0$m" else m.toString()
        val s = currentTime % MINE
        val sStr = if (s < 10) "0$s" else s.toString()
        return "${mStr}:${sStr}/02:10"
    }

}