package com.tson.text.seekbar

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.VectorDrawable
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.tson.text.seekbar.event.Down
import com.tson.text.seekbar.event.Event
import com.tson.text.seekbar.event.Move
import com.tson.text.seekbar.event.Up
import com.tson.text.seekbar.listener.SeekBarViewOnChangeListener
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

        private const val CAP_BUTT = 0x10
        private const val CAP_ROUND = 0x20
        private const val CAP_SQUARE = 0x30
    }

    @Volatile
    private var progress = 0f // 默认进度
    private var isDownUp = false
    @Volatile
    var isEnable = false // 是否禁用，如果 为 true ，禁用， false 不禁用，默认 false 不禁用
    private var mWidth = 0f // 整个进度条宽度 = 0f
    private var mHeight = 0 // 进度条高度
    @Volatile
    private var moveThumb = 0f // 滑动偏移量
    @Volatile
    private var thumbHide = false // 是否隐藏thumb
    private var strokeCap = CAP_ROUND //
    @Volatile
    private var measureLoad = false

    /**
     * 文字画笔
     */
    private var textPaint = Paint()
    private var thumbText = "　" // 默认文字内容
    private var thumbTextSize = 55 // 默认文字大小
    private var thumbTextColor = 0 // 默认文字颜色

    /**
     * 前景进度条画笔
     */
    private var prospectPaint = Paint()
    private var prospectProgressBarHeight = 0 // 前景进度条高度
    private var prospectProgressBarColor = 0 // 前景进度条颜色
    private var prospectProgressBarOffset = 5 // 前景进度条手指拖动时的偏移量，大于0变大，小于0缩小
    private var prospectProgressBarStartColor = 0 // 前景进度条的开始渐变色
    private var prospectProgressBarEndColor = 0 // 前景进度条的开始渐变色

    /**
     * 背景进度条画笔
     */
    private var backgroundPaint = Paint()
    private var backgroundProgressBarHeight = 0 // 背景进度条高度，默认整体高度4分之一
    private var backgroundProgressBarColor = 0 // 背景进度条颜色
    private var backgroundProgressBarOffset = 0 // 背景进度条手指拖动时的偏移量，大于0变大，小于0缩小
    private var backgroundProgressBarStartColor = 0 // 背景进度条的开始渐变色
    private var backgroundProgressBarEndColor = 0 // 背景进度条的结束渐变色

    /**
     * thumb画笔
     */
    private var thumb = Paint()
    private var thumbColor = 0 // 拖动bar前景色
    private var thumbOffset = 0 // 拖动bar的偏移量，大于0变大，小于0缩小
    private var thumbBackgroundStartColor = 0 // 拖动bar前景渐变开始色
    private var thumbBackgroundEndColor = 0  // 拖动bar前景渐变结束色
    private var thumbWidth = 0 // 拖动bar的宽度
    private var useSettingValue = false // 是否有制定宽度，没有则使用text内容填充thumb的宽度，如果有则使用设置的值（不用设置，内部自己判断）
    private var thumbHeight = 0 // 拖动bar的高度，不设置，则根据内容自己填充
    private var thumbType = ROUND // 指定类型，支持方形(矩形)和圆形(圆角)

    /**
     * thumb边框
     */
    private var thumbBorder = Paint()
    private var thumbBorderWidth = 0 // thumb边框宽度
    private var thumbBorderColor = 0 // thumb边框颜色
    private var headEndPadding = 0 // thumb前后padding值
    private var thumbBorderStartColor = 0 // thumb边框渐变开始颜色
    private var thumbBorderEndColor = 0 // thumb边框渐变结束颜色

    private var thumbIcon: Drawable? = null
    private var thumbIconWidth = 0
    private var thumbIconHeight = 0

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

    fun changePercent(percent: Float) {
        changePercent(percent, thumbText)
    }

    fun changePercent(percent: Float, thumbText: String) {
        setPercent(percent, thumbText, true)
    }

    fun setPercent(percent: Float, thumbText: String, force: Boolean = false) {
        if (isDownUp && !force) {
            Log.w(TAG, "click Touching, don't change percent. want to percent=${percent} thumbText=${thumbText}")
            return
        }
        Log.d(TAG, "percent=${percent} thumbText=${thumbText}")
        if (!measureLoad) {
            this.postDelayed({ setPercent(percent, thumbText, force) }, 10)
            return
        }
        moveThumb = percent * mWidth
        this.thumbText = thumbText
        getPercent()
        invalidate()
    }

    constructor(context: Context) : super(context)

    @SuppressLint("ResourceAsColor", "CustomViewStyleable")
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {

        thumbTextColor = Color.parseColor("#ffffff")
        prospectProgressBarColor = Color.parseColor("#336699")
        backgroundProgressBarColor = Color.parseColor("#ededed")
        thumbColor = Color.parseColor("#000000")
        thumbBorderColor = Color.parseColor("#00FFFFFF")

        val seekTypedArray = context.obtainStyledAttributes(attrs, R.styleable.SeekBarView)
        isEnable = seekTypedArray.getBoolean(R.styleable.SeekBarView_touchEnable, false)
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
        thumbBackgroundStartColor = seekTypedArray.getColor(R.styleable.SeekBarView_thumbBackgroundStartColor,0)
        thumbBackgroundEndColor = seekTypedArray.getColor(R.styleable.SeekBarView_thumbBackgroundEndColor,0)
        thumbOffset = seekTypedArray.getDimensionPixelOffset(R.styleable.SeekBarView_thumbOffset, 0)
        thumbWidth = seekTypedArray.getDimensionPixelOffset(R.styleable.SeekBarView_thumbWidth, 0)
        useSettingValue = 0 != thumbWidth
        thumbHeight = seekTypedArray.getDimensionPixelOffset(R.styleable.SeekBarView_thumbHeight, 0)
        thumbType = seekTypedArray.getInt(R.styleable.SeekBarView_thumbType, -1)
        strokeCap = seekTypedArray.getInt(R.styleable.SeekBarView_strokeCap, CAP_ROUND)
        thumbBorderWidth = seekTypedArray.getDimensionPixelOffset(R.styleable.SeekBarView_thumbBorderWidth, 0)
        thumbBorderColor = seekTypedArray.getColor(R.styleable.SeekBarView_thumbBorderColor, thumbBorderColor)
        thumbBorderStartColor = seekTypedArray.getColor(R.styleable.SeekBarView_thumbBorderStartColor,0)
        thumbBorderEndColor = seekTypedArray.getColor(R.styleable.SeekBarView_thumbBorderEndColor,0)
        headEndPadding = seekTypedArray.getDimensionPixelOffset(R.styleable.SeekBarView_headEndPadding,0)
        backgroundProgressBarStartColor = seekTypedArray.getColor(R.styleable.SeekBarView_backgroundProgressBarStartColor,0)
        backgroundProgressBarEndColor = seekTypedArray.getColor(R.styleable.SeekBarView_backgroundProgressBarEndColor,0)
        prospectProgressBarStartColor = seekTypedArray.getColor(R.styleable.SeekBarView_prospectProgressBarStartColor,0)
        prospectProgressBarEndColor = seekTypedArray.getColor(R.styleable.SeekBarView_prospectProgressBarEndColor,0)
        thumbHide = seekTypedArray.getBoolean(R.styleable.SeekBarView_thumbHide, false)
        thumbIcon = seekTypedArray.getDrawable(R.styleable.SeekBarView_thumbIcon)
        thumbIconWidth = seekTypedArray.getDimensionPixelOffset(R.styleable.SeekBarView_thumbIconWidth,0)
        thumbIconHeight = seekTypedArray.getDimensionPixelOffset(R.styleable.SeekBarView_thumbIconHeight, 0)
        val p = seekTypedArray.getInt(R.styleable.SeekBarView_progress, 0)
        progress = (if (p < 0) 0 else if (p > 100) 100 else p).toFloat() / 100f
        seekTypedArray.recycle()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mHeight = MeasureSpec.getSize(heightMeasureSpec)
        if (thumbHeight == 0) {
            thumbHeight = mHeight
        }
        mWidth = measuredWidth.toFloat()
        moveThumb = mWidth * progress
        measureLoad = true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawBgProgress(canvas)
        drawUpProgress(canvas)
        if (!thumbHide) {
            drawThumb(canvas)
        }
    }

    private fun checkPercent(x: Float, eventType: Event) {
        touch(x / mWidth, eventType)
    }

    private fun verifyBound(event: MotionEvent): Boolean {
        if (isDownUp) {
            return true
        }
        val y = measuredHeight
        var yRange =
            if (prospectProgressBarHeight > backgroundProgressBarHeight) prospectProgressBarHeight.toFloat() else backgroundProgressBarHeight.toFloat()
        if (!thumbHide) { //如果隐藏了，则不用比较有效范围值，如果没有，则直接使用背景或前景进度条的
            val tbHeight = if (thumbBorderWidth != 0) thumbBorder.strokeWidth else thumb.strokeWidth
            yRange = if (tbHeight > yRange) tbHeight else yRange
        }
        val upBoundary = (y - yRange) / 2 // 上边界
        val downBoundary = y - upBoundary // 下边界
        return event.y in upBoundary..downBoundary // 验证是否在范围以内
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (checkIsEnable()) {
            return true
        }
        if (!verifyBound(event)) { // 如果在边界之外
            return true
        }
        touchEvent(event)
        return true
    }

    fun touchEvent(event: MotionEvent) {
        val x = event.x
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                isDownUp = true
                moveThumb = x
                invalidate()
                checkPercent(x, Down)
            }
            MotionEvent.ACTION_MOVE -> {
                moveThumb = x
                invalidate()
                checkPercent(x, Move)
            }
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                isDownUp = false
                moveThumb = x
                invalidate()
                checkPercent(x, Up)
            }
        }
    }

    /**
     * 绘制前景进度条
     */
    @SuppressLint("ResourceAsColor")
    private fun drawUpProgress(canvas: Canvas) {
        val hy = (mHeight / 2).toFloat()
        val pw = ((if (isDownUp) prospectProgressBarHeight + prospectProgressBarOffset else prospectProgressBarHeight) / 2).toFloat()
        prospectPaint.color = prospectProgressBarColor
        prospectPaint.style = Paint.Style.FILL_AND_STROKE
        prospectPaint.strokeWidth = pw * 2
        prospectPaint.strokeCap = getStrokeCap()
        prospectPaint.strokeJoin = Paint.Join.BEVEL
        prospectPaint.isAntiAlias = true

        val endX = if (getStrokeCap() == Paint.Cap.BUTT) {
            moveThumb
        } else {
            val end = if (moveThumb < pw) pw else moveThumb.coerceAtMost(mWidth - pw)
            end
        }

        if (prospectProgressBarStartColor != 0 || prospectProgressBarEndColor != 0) {
            val intArray = mutableListOf<Int>()
            if (prospectProgressBarStartColor != 0) {
                intArray.add(prospectProgressBarStartColor)
            }
            if (prospectProgressBarColor != 0) {
                intArray.add(prospectProgressBarColor)
            }
            if (prospectProgressBarEndColor != 0) {
                intArray.add(prospectProgressBarEndColor)
            }
            val intArraySize = intArray.size
            val floatArray = mutableListOf<Float>()
            intArray.forEachIndexed { index, _ ->
                when (intArraySize - index) {
                    intArraySize -> floatArray.add(0f)
                    1 -> floatArray.add(1f)
                    else -> floatArray.add(0.5f)
                }
            }
            val linearGradient = LinearGradient(0f, 0f, endX, 0f, intArray.toIntArray(), floatArray.toFloatArray(), Shader.TileMode.CLAMP)
            prospectPaint.shader = linearGradient
        } else {
            prospectPaint.color = prospectProgressBarColor
        }

        val path = Path()
        if (getStrokeCap() == Paint.Cap.BUTT) {
            path.moveTo(0f, hy)
            path.lineTo(endX, hy)
        } else {
            path.moveTo(pw+4, hy)
            path.lineTo(endX, hy)
        }
        canvas.drawPath(path, prospectPaint)
    }

    private fun getStrokeCap(): Paint.Cap {
        return when (strokeCap) {
            CAP_BUTT -> Paint.Cap.BUTT
            CAP_ROUND -> Paint.Cap.ROUND
            else -> Paint.Cap.SQUARE
        }
    }

    /**
     * 绘制背景进度条
     */
    @SuppressLint("ResourceAsColor")
    private fun drawBgProgress(canvas: Canvas) {
        val hy = (mHeight / 2).toFloat()
        val width = (if (isDownUp) backgroundProgressBarHeight + backgroundProgressBarOffset else backgroundProgressBarHeight).toFloat()
        backgroundPaint.style = Paint.Style.FILL_AND_STROKE
        backgroundPaint.strokeWidth = width
        backgroundPaint.strokeCap = getStrokeCap()
        backgroundPaint.strokeJoin = Paint.Join.BEVEL
        backgroundPaint.isAntiAlias = true
        if (backgroundProgressBarStartColor != 0 || backgroundProgressBarEndColor != 0) {
            val intArray = mutableListOf<Int>()
            if (backgroundProgressBarStartColor != 0) {
                intArray.add(backgroundProgressBarStartColor)
            }
            if (backgroundProgressBarColor != 0) {
                intArray.add(backgroundProgressBarColor)
            }
            if (backgroundProgressBarEndColor != 0) {
                intArray.add(backgroundProgressBarEndColor)
            }
            val intArraySize = intArray.size
            val floatArray = mutableListOf<Float>()
            intArray.forEachIndexed { index, _ ->
                when (intArraySize - index) {
                    intArraySize -> floatArray.add(0f)
                    1 -> floatArray.add(1f)
                    else -> floatArray.add(0.5f)
                }
            }
            val linearGradient = LinearGradient(0f, 0f, measuredWidth.toFloat(), 0f, intArray.toIntArray(), floatArray.toFloatArray(), Shader.TileMode.CLAMP)
            backgroundPaint.shader = linearGradient
        } else {
            backgroundPaint.color = backgroundProgressBarColor
        }
        val path = Path()

        if (getStrokeCap() == Paint.Cap.BUTT) {
            path.moveTo(0f, hy)
            path.lineTo(mWidth, hy)
        } else {
            path.moveTo(width / 2, hy)
            path.lineTo(mWidth - width / 2 , hy)
        }
        canvas.drawPath(path, backgroundPaint)
    }

    /**
     * 绘制thumb和文本
     */
    @SuppressLint("ResourceAsColor")
    private fun drawThumb(canvas: Canvas) {
        textPaint.textSize = thumbTextSize.toFloat()
        val tw = textPaint.measureText(thumbText)
        // 确认是否有自定义高度，如果没有，则使用默认高度
        if (!useSettingValue) {
            thumbWidth = tw.toInt()
        }
        // 拿到thumb两端圆角的半径
        val p0 = if (thumbHeight != 0) (thumbHeight / 2).toFloat() else (mHeight / 2).toFloat()
        // 边框和padding值都是要占宽度的
        val externalSize = headEndPadding + thumbBorderWidth
        // thumb的中点
        val tbw = thumbWidth / 2
        if (moveThumb < tbw + p0 + externalSize) {
            // 如果滑动点，在0 - thumb 一半以下，小于最小有效点，则为无效，重置为最小的有效点，即thumb宽度的一半
            moveThumb = tbw + p0 + externalSize
        } else if (moveThumb > mWidth - (tbw + p0 + externalSize)) {
            // 如果滑动点 在总宽度减去thumb一半以上，超过最大的有效点，则为无效，重置为  最大的有效点
            moveThumb = mWidth - (tbw + p0 + externalSize)
        }
        // 开始绘制的点，thumb绘制的点为当前的点减去thumb一半宽度，让点击/滑动的点在thumb的中间
        val currentStart = moveThumb - tbw - headEndPadding
        val currentEnd = moveThumb + tbw + headEndPadding

        drawBorder(canvas, currentStart, currentEnd, p0)
        drawThumbDetail(canvas, currentStart, currentEnd, p0)
        drawText(canvas, tw, p0, externalSize)
        drawIcon(canvas, currentStart, currentEnd)
    }

    /**
     * 绘制Thumb
     */
    private fun drawThumbDetail(canvas: Canvas, currentStart: Float, currentEnd: Float, p0: Float) {
        thumb.style = Paint.Style.FILL_AND_STROKE
        // 判断是否是触摸状态，如果是触摸状态，则使用正常高度减去偏移量，反之使用正常高度
        thumb.strokeWidth =
            if (isDownUp) thumbHeight.toFloat() - thumbOffset else thumbHeight.toFloat()
        // 根据设置的类型，觉得thumb块是矩形还是圆形画笔
        thumb.strokeCap =
            if (thumbType == ROUND) Paint.Cap.ROUND else Paint.Cap.SQUARE // 方块或者 圆形
        thumb.strokeJoin = Paint.Join.BEVEL
        thumb.isAntiAlias = true
        thumb.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OVER)

        if (thumbBackgroundStartColor != 0 || thumbBackgroundEndColor != 0) {
            val intArray = mutableListOf<Int>()
            if (thumbBackgroundStartColor != 0) {
                intArray.add(thumbBackgroundStartColor)
            }
            if (thumbColor != 0) {
                intArray.add(thumbColor)
            }
            if (thumbBackgroundEndColor != 0) {
                intArray.add(thumbBackgroundEndColor)
            }
            val intArraySize = intArray.size
            val floatArray = mutableListOf<Float>()
            intArray.forEachIndexed { index, _ ->
                when (intArraySize - index) {
                    intArraySize -> floatArray.add(0f)
                    1 -> floatArray.add(1f)
                    else -> floatArray.add(0.5f)
                }
            }
            val linearGradient = LinearGradient((currentStart-p0), 0f, (currentEnd +p0 ), 0f, intArray.toIntArray(), floatArray.toFloatArray(), Shader.TileMode.CLAMP)
            thumb.shader = linearGradient
        } else {
            thumb.color = thumbColor
        }

        val path1 = Path()
        path1.moveTo(currentStart, (mHeight / 2).toFloat())
        path1.lineTo(currentEnd, (mHeight / 2).toFloat())
        canvas.drawPath(path1, thumb)
    }

    /**
     * 绘制边框
     */
    private fun drawBorder(canvas: Canvas, currentStart: Float, currentEnd: Float, p0: Float) {
        //边框
        if (thumbBorderWidth > 0) {
            thumbBorder.style = Paint.Style.FILL_AND_STROKE
            // 判断是否是触摸状态，如果是触摸状态，则使用正常高度减去偏移量，反之使用正常高度
            thumbBorder.strokeWidth =
                if (isDownUp) thumbHeight.toFloat() - thumbOffset + thumbBorderWidth * 2 else thumbHeight.toFloat() + thumbBorderWidth * 2
            // 根据设置的类型，觉得thumb块是矩形还是圆形画笔
            thumbBorder.strokeCap =
                if (thumbType == ROUND) Paint.Cap.ROUND else Paint.Cap.SQUARE // 方块或者 圆形
            thumbBorder.strokeJoin = Paint.Join.BEVEL
            thumbBorder.isAntiAlias = true
            thumbBorder.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OVER)

            if (thumbBorderStartColor != 0 || thumbBorderEndColor != 0) {
                val intArray = mutableListOf<Int>()
                if (thumbBorderStartColor != 0) {
                    intArray.add(thumbBorderStartColor)
                }
                if (thumbBorderColor != 0) {
                    intArray.add(thumbBorderColor)
                }
                if (thumbBorderEndColor != 0) {
                    intArray.add(thumbBorderEndColor)
                }
                val intArraySize = intArray.size
                val floatArray = mutableListOf<Float>()
                intArray.forEachIndexed { index, _ ->
                    when (intArraySize - index) {
                        intArraySize -> floatArray.add(0f)
                        1 -> floatArray.add(1f)
                        else -> floatArray.add(0.5f)
                    }
                }
                val linearGradient = LinearGradient((currentStart-p0-thumbBorderWidth), 0f, (currentEnd +p0+thumbBorderWidth), 0f, intArray.toIntArray(), floatArray.toFloatArray(), Shader.TileMode.CLAMP)
                thumbBorder.shader = linearGradient
            } else {
                thumbBorder.color = thumbBorderColor
            }

            val path2 = Path()
            path2.moveTo(currentStart, (mHeight / 2).toFloat())
            path2.lineTo(currentEnd, (mHeight / 2).toFloat())
            canvas.drawPath(path2, thumbBorder)
        }
    }

    /**
     * 绘制文字
     */
    private fun drawText(canvas: Canvas, tw: Float, p0: Float, externalSize: Int) {
        // 文字绘制
        textPaint.color = thumbTextColor
        textPaint.style = Paint.Style.FILL
        textPaint.strokeCap = Paint.Cap.BUTT
        textPaint.textAlign = Paint.Align.LEFT
        textPaint.strokeJoin = Paint.Join.BEVEL
        textPaint.isAntiAlias = true
        textPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OVER)

        val fm = textPaint.fontMetrics
        // 获取文字所占高度
        val th = ceil(fm.descent - fm.ascent.toDouble()).toInt()
        // 获取文字到Y原点的距离
        val thy = mHeight / 2 + (th / 3.3).toFloat()
        // 获取thumb的中间点，使文字能跟随thumb而不错位
        val tbw2 = tw / 2
        // 以下同thumb的有效滑动范围逻辑
        if (moveThumb < tbw2 + p0 + externalSize) {
            moveThumb = tbw2 + p0 + externalSize
        } else if (moveThumb > mWidth - (tbw2 + p0 + externalSize)) {
            moveThumb = mWidth - (tbw2 + p0 + externalSize)
        }
        // 文字的开始点，因为获取过宽度，以及中间点，所以只需要拿到开始点即可确认整个文本的位置
        val currentStart2 = moveThumb - tbw2
        // 进行绘制
        canvas.drawText(thumbText, currentStart2, thy, textPaint)
    }

    /**
     * 绘制图标
     */
    private fun drawIcon(canvas: Canvas,currentStart: Float, currentEnd: Float) {
        thumbIcon?.let { drawable ->
            val bitmap = Bitmap.createBitmap(
                if (thumbIconWidth <= 0) drawable.intrinsicWidth else thumbIconWidth,
                if (thumbIconHeight <= 0) drawable.intrinsicHeight else thumbIconHeight,
                Bitmap.Config.ARGB_8888
            )
            val nc = Canvas(bitmap)
            drawable.setBounds(0, 0, nc.width, nc.height)
            drawable.draw(nc)
            val centerPoint = (currentEnd - currentStart) / 2 + currentStart
            val width = if (thumbIconWidth <= 0) bitmap.width else thumbIconWidth
            val height = if ( thumbIconHeight <= 0) bitmap.height else thumbIconHeight
            val yPoint = mHeight.toFloat() / 2
            val top = yPoint - height / 2
            val iconStart = centerPoint - width / 2
            canvas.drawBitmap(bitmap, iconStart, top, Paint())
        }
    }

}