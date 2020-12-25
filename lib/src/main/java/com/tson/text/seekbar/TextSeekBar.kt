package com.tson.text.seekbar

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.tson.text.seekbar.SeekBarViewOnChangeListener.Companion.DOWN
import com.tson.text.seekbar.SeekBarViewOnChangeListener.Companion.MOVE
import com.tson.text.seekbar.SeekBarViewOnChangeListener.Companion.UP
import kotlin.math.ceil

/**
 *  Date 2020/12/25 1:54 PM
 *
 * @author Tson
 */
class TextSeekBar : View {

    companion object {
        private const val TAG = "SeekBarView"
        private const val ROUND = 0x20
        private const val SQUARE = 0x10
    }

    private var progress = 0f // 默认进度
    private var isDownUp = false
    var isEnable = false // 是否禁用，如果 为 true ，禁用， false 不禁用，默认 false 不禁用
    private var mWidth = 0f // 整个进度条宽度 = 0f
    private var mHeight = 0 // 进度条高度
    private var thumbText = "　　"
    private var thumbTextSize = 55
    private var thumbTextColor = 0

    /**
     * 文字画笔
     */
    private var textPaint = Paint()
    private var prospectProgressBarHeight = 0
    private var prospectProgressBarColor = 0
    private var prospectProgressBarOffset = 5

    /**
     * 背景进度条画笔
     */
    private var prospectPaint = Paint()
    private var backgroundProgressBarHeight = 0 // 背景进度条高度，默认整体高度4分之一
    private var backgroundProgressBarColor = 0 // 背景
    private var backgroundProgressBarOffset = 0

    /**
     * 背景进度条画笔
     */
    private var backgroundPaint = Paint()
    private var thumbColor = 0 // 拖动bar前景色
    private var thumbOffset = 10
    private var thumbWidth = 0 // 拖动bar的宽度
    private var useSettingValue = false

    /**
     * thumb画笔
     */
    private var thumb = Paint()
    private var moveThumb = 0f // 滑动偏移量
    private var thumbHeight = 0
    private var thumbType = ROUND

    private var seekBarTouchListener: SeekBarViewOnChangeListener? = null

    fun addOnChangeListener(listener: SeekBarViewOnChangeListener) {
        seekBarTouchListener = listener
    }

    fun removeOnChangeListener() {
        seekBarTouchListener = null
    }

    private fun touch(percent: Float, eventType: Int) {
        seekBarTouchListener?.touch(
            if (percent < 0) 0f else if (percent > 1) 1f else percent,
            eventType
        )
    }

    private fun checkIsEnable(): Boolean {
        if (isEnable) {
            Log.w(TAG, "isEnable = true")
        }
        return isEnable
    }

    fun getPercent(): Float {
        if (mWidth == 0f || moveThumb == 0f) {
            return 0f
        }
        progress = moveThumb / mWidth
        return progress
    }

    fun setPercent(percent: Float) {
        setPercent(percent, thumbText)
    }

    fun setPercent(percent: Float, thumbText: String) {
        if (isDownUp) {
            Log.w(TAG, "click Touching, don't change percent. want to percent=${percent} thumbText=${thumbText}")
            return
        }
        Log.d(TAG, "percent=${percent} thumbText=${thumbText}")
        moveThumb = percent * mWidth
        this.thumbText = thumbText
        getPercent()
        invalidate()
    }

    constructor(context: Context) : super(context)

    @SuppressLint("Recycle", "ResourceAsColor", "CustomViewStyleable")
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {

        thumbTextColor = Color.parseColor("#ffffff")
        prospectProgressBarColor = Color.parseColor("#336699")
        backgroundProgressBarColor = Color.parseColor("#ededed")
        thumbColor = Color.parseColor("#000000")

        val seekTypedArray = context.obtainStyledAttributes(attrs, R.styleable.SeekBarView)
        thumbText = seekTypedArray.getString(R.styleable.SeekBarView_thumbText) ?: ""
        thumbTextSize = seekTypedArray.getDimensionPixelOffset(R.styleable.SeekBarView_thumbTextSize, 26)
        thumbTextColor = seekTypedArray.getColor(R.styleable.SeekBarView_thumbTextColor, thumbTextColor)
        prospectProgressBarHeight = seekTypedArray.getDimensionPixelOffset(R.styleable.SeekBarView_prospectProgressBarHeight, 10)
        prospectProgressBarColor = seekTypedArray.getColor(R.styleable.SeekBarView_prospectProgressBarColor, prospectProgressBarColor)
        prospectProgressBarOffset = seekTypedArray.getDimensionPixelOffset(R.styleable.SeekBarView_prospectProgressBarOffset, 5)
        backgroundProgressBarHeight = seekTypedArray.getDimensionPixelOffset(R.styleable.SeekBarView_backgroundProgressBarHeight, 10)
        backgroundProgressBarColor = seekTypedArray.getColor(R.styleable.SeekBarView_backgroundProgressBarColor, backgroundProgressBarColor)
        backgroundProgressBarOffset = seekTypedArray.getDimensionPixelOffset(R.styleable.SeekBarView_backgroundProgressBarOffset, 0)
        thumbColor = seekTypedArray.getColor(R.styleable.SeekBarView_thumbBackgroundColor, thumbColor)
        thumbOffset = seekTypedArray.getDimensionPixelOffset(R.styleable.SeekBarView_thumbOffset, 0)
        thumbWidth = seekTypedArray.getDimensionPixelOffset(R.styleable.SeekBarView_thumbWidth, 0)
        useSettingValue = 0 != thumbWidth
        thumbHeight = seekTypedArray.getDimensionPixelOffset(R.styleable.SeekBarView_thumbHeight, 0)
        thumbType = seekTypedArray.getInt(R.styleable.SeekBarView_thumbType,-1)
        val p = seekTypedArray.getInt(R.styleable.SeekBarView_progress, 0)
        progress = (if (p < 0) 0 else if (p > 100) 100 else p).toFloat() / 100f
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mHeight = MeasureSpec.getSize(heightMeasureSpec)
        if (thumbHeight == 0) {
            thumbHeight = mHeight
        }
        mWidth = measuredWidth.toFloat()
        moveThumb = mWidth * progress
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawBgProgress(canvas)
        drawUpProgress(canvas)
        drawThumb(canvas)
    }

    private fun checkPercent(x: Float, eventType: Int) {
        touch(x / mWidth, eventType)
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (checkIsEnable()) {
            return true
        }
        val y = event.y
        val x = event.x
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                isDownUp = true
                moveThumb = x
                invalidate()
                checkPercent(x, DOWN)
            }
            MotionEvent.ACTION_MOVE -> {
                moveThumb = x
                invalidate()
                checkPercent(x, MOVE)
            }
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                isDownUp = false
                moveThumb = x
                invalidate()
                checkPercent(x, UP)
            }
            else -> {
            }
        }
        return true
    }

    /**
     * 绘制前景进度条
     */
    @SuppressLint("ResourceAsColor")
    private fun drawUpProgress(canvas: Canvas) {
        val hy = (mHeight / 2).toFloat()
        val pw =
            ((if (isDownUp) prospectProgressBarHeight + prospectProgressBarOffset else prospectProgressBarHeight) / 2).toFloat()
        prospectPaint.color = prospectProgressBarColor
        prospectPaint.style = Paint.Style.FILL_AND_STROKE
        prospectPaint.strokeWidth = pw * 2
        prospectPaint.strokeCap = Paint.Cap.ROUND
        prospectPaint.strokeJoin = Paint.Join.BEVEL
        prospectPaint.isAntiAlias = true
        val endX: Float
        endX = if (moveThumb < pw) {
            pw
        } else Math.min(moveThumb, mWidth - pw)
        val path = Path()
        path.moveTo(pw, hy)
        path.lineTo(endX - 4, hy)
        canvas.drawPath(path, prospectPaint)
    }

    /**
     * 绘制背景进度条
     */
    @SuppressLint("ResourceAsColor")
    private fun drawBgProgress(canvas: Canvas) {
        val hy = (mHeight / 2).toFloat()
        val width =
            (if (isDownUp) backgroundProgressBarHeight + backgroundProgressBarOffset else backgroundProgressBarHeight).toFloat()
        backgroundPaint.color = backgroundProgressBarColor
        backgroundPaint.style = Paint.Style.FILL_AND_STROKE
        backgroundPaint.strokeWidth = width
        backgroundPaint.strokeCap = Paint.Cap.ROUND
        backgroundPaint.strokeJoin = Paint.Join.BEVEL
        backgroundPaint.isAntiAlias = true
        val path = Path()
        path.moveTo(width / 2, hy)
        path.lineTo(mWidth - width / 2 - 4, hy)
        canvas.drawPath(path, backgroundPaint)
    }

    /**
     * 绘制thumb和文本
     */
    @SuppressLint("ResourceAsColor")
    private fun drawThumb(canvas: Canvas) {
        textPaint.textSize = thumbTextSize.toFloat()
        val tw = textPaint.measureText(thumbText)
        if (!useSettingValue) {
            thumbWidth = tw.toInt()
        }
        val p0 = if (thumbHeight != 0) (thumbHeight / 2).toFloat() else (mHeight / 2).toFloat()
        val tbw = thumbWidth / 2
        if (moveThumb < tbw + p0) {
            moveThumb = tbw + p0
        } else if (moveThumb > mWidth - (tbw + p0)) {
            moveThumb = mWidth - (tbw + p0)
        }
        val currentStart = moveThumb - tbw
        val currentEnd = moveThumb + tbw
        thumb.color = thumbColor
        thumb.style = Paint.Style.FILL_AND_STROKE
        thumb.strokeWidth =
            if (isDownUp) thumbHeight.toFloat() - thumbOffset else thumbHeight.toFloat()
        thumb.strokeCap = if (thumbType == ROUND) Paint.Cap.ROUND else Paint.Cap.SQUARE // 方块或者 圆形
        thumb.strokeJoin = Paint.Join.BEVEL
        thumb.isAntiAlias = true
        thumb.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OVER)
        val path1 = Path()
        path1.moveTo(currentStart, (mHeight / 2).toFloat())
        path1.lineTo(currentEnd, (mHeight / 2).toFloat())
        canvas.drawPath(path1, thumb)

        textPaint.color = thumbTextColor
        textPaint.style = Paint.Style.FILL
        textPaint.strokeCap = Paint.Cap.BUTT
        textPaint.textAlign = Paint.Align.LEFT
        textPaint.strokeJoin = Paint.Join.BEVEL
        textPaint.isAntiAlias = true
        textPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OVER)
        val fm = textPaint.fontMetrics
        val th = ceil(fm.descent - fm.ascent.toDouble()).toInt()
        val thy = mHeight / 2 + (th / 3.3).toFloat()
        val tbw2 = tw / 2
        if (moveThumb < tbw2 + p0) {
            moveThumb = tbw2 + p0
        } else if (moveThumb > mWidth - (tbw2 + p0)) {
            moveThumb = mWidth - (tbw2 + p0)
        }
        val currentStart2 = moveThumb - tbw2
        canvas.drawText(thumbText, currentStart2, thy, textPaint)
    }

}

interface SeekBarViewOnChangeListener {

    companion object {
        const val DOWN = 1
        const val MOVE = 2
        const val UP = 3
    }

    fun touch(percent: Float, eventType: Int)

}