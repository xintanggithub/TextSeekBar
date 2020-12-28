- 目的是什么？
        因为系统默认的SeekBar的thumb虽然是支持Drawable的，但是我们有的时候需要把文字显示在thumb上面，比如某些播放进度条，需要显示播放进度和缓冲状态等，这种情况如果依然使用默认的SeekBar就合适了，虽然可以通过动态生成drawable进行更新thumb实现，但是无论从性能还是实用性，都是不可取的。
        所以遇到这种情况，就需要自定义view，实现一些定制化的需求。
        
- 说明
​		由于前景进度和背景进度没有什么逻辑，所以此处先对thumb进行说明，其实thumb的逻辑也很简单，主要是一些阈值范围的确定，本文主要是用于给新手开发拓展代码思维。

- 结构示意图

![示例](https://github.com/xintanggithub/TextSeekBar/blob/master/textSeekBar2.png?raw=true)

椭圆thumb后面简称TB。

D为原点坐标（0，0）

- 创建自定义类
```
class TextSeekBar : View {

    private var mWidth = 0f // 整个进度条宽度 = 0f
    private var mHeight = 0 // 进度条高度

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int)
                : super(context, attrs, defStyleAttr)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mHeight = MeasureSpec.getSize(heightMeasureSpec)
        mWidth = measuredWidth.toFloat()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawThumb(canvas)
    }

    private fun drawThumb(canvas: Canvas) {
    
    }

}
```


- 绘制出TB我们需要一些什么东西？
1. 开始点 F
2. TB宽度 tbw(通过自定义属性设置)
3. TB高度 tbh(通过自定义属性设置)

从上面可以看到，其实高度，宽度都可以当成是已知的，只需要获取初始点F便可以绘制出TB，而且现在默认是让TB垂直居中的，所以F点我们可以很轻松拿到。

```
DF = CD * 1/2 = mHeight / 2
则F坐标为 (0, mHeight / 2)

在drawThumb方法中进行绘制

        // 模拟自定义属性设置宽度
        val tbw = 160
        // 模拟高度为默认高度
        val tbh = mHeight

        var thumb = Paint()
        thumb.color = Color.parseColor("#000000")
        thumb.style = Paint.Style.FILL_AND_STROKE
        thumb.strokeWidth = tbh
        thumb.strokeCap = Paint.Cap.ROUND
        thumb.strokeJoin = Paint.Join.BEVEL
        thumb.isAntiAlias = true
        thumb.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OVER)

        val path = Path()
        path.moveTo(0,mHeight / 2).toFloat())
        path.lineTo(tbw,mHeight / 2).toFloat())
        canvas.drawPath(path, thumb)
```

如上便成功绘制出了TB

- 如何让TB动起来？
1. 动态变更开始点
2. 重新绘制

动态变换开始点，即根据手指触碰的位置去动态绘制TB到当前的点触点的位置，所以在这之前，需要知道手指触碰到哪儿了，是滑动状态还是处于按下状态或是抬起状态：
```
    private var moveThumb = 0f // 滑动偏移量

    // 添加事件监听
    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        val y = event.y
        val x = event.x
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                moveThumb = x
            }
            MotionEvent.ACTION_MOVE -> {
                moveThumb = x
            }
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                moveThumb = x
            }
        }
        return true
    }
```

知道了滑动的点之后，现在就是要开始去进行动态计算起始点的位置：

```
// 在上面绘制TB的地方，加上上面监听的滑动位置moveThumb，将横坐标修改成动态的：

        val path = Path()
        path.moveTo(moveThumb,mHeight / 2).toFloat())
        path.lineTo(moveThumb + tbw,mHeight / 2).toFloat())
        canvas.drawPath(path, thumb)
```

然后进行动态刷新，调用invalidate()方法即可，那么在哪里刷新呢？当然是滑动位置发生变化的时候，修改事件监听方法代码：
```
    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        val y = event.y
        val x = event.x
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                moveThumb = x
                invalidate() //更新UI
            }
            MotionEvent.ACTION_MOVE -> {
                moveThumb = x
                invalidate() //更新UI
            }
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                moveThumb = x
                invalidate() //更新UI
            }
        }
        return true
    }
```
现在看一下，是不是可以跟着手指动起来了？

等等，是不是发现一点问题？那就是这个TB咋一直在你的手指右边呢？一般的不都是手指在TB中间吗？所以我们回来再检查一下。

是不是发现问题了？
没错，现在TB的位置起始点就是在滑动的位置，所以导致这个问题，要解决也很简单，把起始的位置往前挪一挪就好了。
既然是要到中间点，则往前挪半个TB宽度即可，即FI的宽度：

```
FI = FG + GI
// 因为是圆形画笔，则FG 等于TB高度的一半
FG = tbh /2
GI = tbw /2 
FI = tbw /2 + tbh /2

// 修改如下：
        val offsetX = tbw / 2 + tbh /2

        val path = Path()
        path.moveTo(moveThumb - offsetX,mHeight / 2).toFloat())
        path.lineTo(moveThumb + tbw - offsetX,mHeight / 2).toFloat())
        canvas.drawPath(path, thumb)
```

改完之后就可以以你的手指为中心点滑动了。

再仔细看看是不是发现还有问题？那就是这个TB为啥可以划出边界？看样子还需要限制一下滑动的距离，不能随随便便的这样跑偏了。

在要满足TB以手指为中心，且无法滑动出边界，则可以得到实际滑动范围是F-E，而有效的滑动范围是I-H。
所以得出来的结论是：FG和HE这两段距离内的滑动需要做限制。
```
        if (moveThumb < tbw / 2 + tbh /2) {
            // 如果滑动点，在0 - thumb 一半以下，小于最小有效点，则为无效，重置为最小的有效点，即thumb宽度的一半
            moveThumb = tbw / 2 + tbh /2
        } else if (moveThumb > mWidth - (tbw / 2 + tbh /2)) {
            // 如果滑动点 在总宽度减去thumb一半以上，超过最大的有效点，则为无效，重置为  最大的有效点
            moveThumb = mWidth - (tbw / 2 + tbh /2)
        }
```

到这里，基本就完成了。文字和TB的逻辑基本一致，只不过一个是图形画笔，一个是文字的，这里需要注意点，其他的就是一样的。
