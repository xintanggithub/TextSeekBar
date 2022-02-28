package com.tson.text.seekbar

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.tson.text.seekbar.event.Down
import com.tson.text.seekbar.event.Event
import com.tson.text.seekbar.event.Move
import com.tson.text.seekbar.event.Up
import com.tson.text.seekbar.listener.SeekBarViewOnChangeListener

class MultiSeekBar : RelativeLayout {

    private val multiView: LinearLayout by lazy {
        val view: LinearLayout = inflate(context, R.layout.mutl, null) as LinearLayout
        view.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        view
    }

    private var backgroundProgress: Drawable? = null
    private var backgroundProgressHeight = 5
    private var prospectProgress: Drawable? = null
    private var prospectProgressHeight = 5
    private val pbLL = LinearLayout(context)

    @Volatile
    var isEnable = false // 是否禁用，如果 为 true ，禁用， false 不禁用，默认 false 不禁用
    private var mWidth = 0f
    private var mtPercent = 0f
    private var tsb: TextSeekBar? = null
    private val startV: View by lazy { multiView.findViewById<View>(R.id.startV) }
    private val endV: View by lazy { multiView.findViewById<View>(R.id.endV) }
    private val customV: LinearLayout by lazy { multiView.findViewById<LinearLayout>(R.id.customV) }

    private var seekBarTouchListener: SeekBarViewOnChangeListener? = null

    fun addOnChangeListener(listener: SeekBarViewOnChangeListener) {
        seekBarTouchListener = listener
    }

    fun removeOnChangeListener() {
        seekBarTouchListener = null
    }

    private fun touch(percent: Float, eventType: Event) {
        seekBarTouchListener?.touch(if (percent < 0) 0f else if (percent > 1) 1f else percent, eventType)
    }

    fun percent(percent: Float) {
        changePercent(percent, null)
        tsb?.setPercent(percent)
    }

    private fun changePercent(percent: Float, event: Event?) {
        val p = if (percent < 0f) 0f else if (percent > 1f) 1f else percent
        mtPercent = p
        Log.w("changePercent","changePercent = $p")
        changeWeight(startV, 100 * p)
        changeWeight(endV, 100 - (100 * p))
        prospectProgress?.let { changeProgress() }
        event?.let { touch(p, it) }
    }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        val seekTypedArray = context.obtainStyledAttributes(attrs, R.styleable.MultiSeekBar)
        backgroundProgress = seekTypedArray.getDrawable(R.styleable.MultiSeekBar_backgroundProgress)
        prospectProgress = seekTypedArray.getDrawable(R.styleable.MultiSeekBar_prospectProgress)
        backgroundProgressHeight = seekTypedArray.getDimensionPixelOffset(R.styleable.MultiSeekBar_backgroundProgressHeight,5)
        prospectProgressHeight = seekTypedArray.getDimensionPixelOffset(R.styleable.MultiSeekBar_prospectProgressHeight,5)
        seekTypedArray.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mWidth = measuredWidth.toFloat()
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (isEnable) {
            return true
        }
        val x = event.x
        val p = x / mWidth

        when (event.action) {
            MotionEvent.ACTION_DOWN -> changePercent(p, Down)
            MotionEvent.ACTION_MOVE -> changePercent(p, Move)
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> changePercent(p, Up)
        }

        tsb?.touchEvent(event)
        return true
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        drawProcess()
        drawThumb()
    }

    private fun drawProcess() {
        val bgLL = LinearLayout(context)
        bgLL.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, backgroundProgressHeight).also { it.addRule(CENTER_VERTICAL) }
        bgLL.background = backgroundProgress
        addView(bgLL)

        changeProgress()
        pbLL.background = prospectProgress
        addView(pbLL)
    }

    private fun changeProgress() {
        pbLL.layoutParams = LayoutParams((mWidth * mtPercent).toInt(), prospectProgressHeight).also { it.addRule(CENTER_VERTICAL) }
    }


    private fun drawThumb() {
        if (childCount > 0) {
            val thumb = getChildAt(0)
            removeView(thumb)

            addView(multiView)
            multiView.weightSum = 100f

            changeWeight(startV, 0f)
            changeWeight(endV, 100f)
            customV.addView(thumb)

            for (i in 0..childCount) {
                val item = getChildAt(i)
                if (item is TextSeekBar) {
                    tsb = item
                    tsb?.isEnable = true
                    tsb?.changePercent(0f)
                }
            }

        }
    }

    private fun changeWeight(view: View, weight: Float) {
        val lp = view.layoutParams as LinearLayout.LayoutParams
        lp.weight = weight
        view.layoutParams = lp
    }

}