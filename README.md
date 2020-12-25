### TextSeekBar

#### 示例

![示例](https://github.com/xintanggithub/TextSeekBar/blob/master/use_screen.gif?raw=true)


#### 1.使用

```xml
    <com.tson.text.seekbar.TextSeekBar
        android:id="@+id/seekbar"
        android:layout_width="match_parent"
        android:layout_height="30dp"
        android:layout_margin="10dp"
        app:progress="20"
        app:thumbHeight="20dp"
        app:thumbOffset="2dp"
        app:thumbText="thumbText"
        app:thumbType="round"
        app:thumbWidth="60dp" />
```



#### 2.参数说明

| 自定义属性                  | 说明                                                         |
| --------------------------- | ------------------------------------------------------------ |
| thumbText                   | thumbText的文本内容，代码中可通过setPercent修改              |
| thumbTextSize               | thumbText的字体大小                                          |
| thumbTextColor              | thumbText订单字体颜色                                        |
| prospectProgressBarHeight   | 前景进度条高度                                               |
| prospectProgressBarColor    | 前景进度条颜色                                               |
| prospectProgressBarOffset   | 前景进度条触发偏移量，如：触碰前高度为10，触碰后高度变为10+该属性值 ，体现触摸反馈，设置为0，则无效果 |
| backgroundProgressBarHeight | 背景进度条高度                                               |
| backgroundProgressBarColor  | 背景进度条颜色                                               |
| backgroundProgressBarOffset | 背景进度条触发偏移量                                         |
| thumbBackgroundColor        | thumbBar的背景色                                             |
| thumbOffset                 | thumbBar的触发偏移量，和前景与背景不同，这个是当前高度减去当前偏移量 |
| thumbWidth                  | thumbBar的宽度，如果不设置，则宽度基于thumbText动态生成 ，如果也没设置thumbText，则thumbBar根据thumbType显示为正方形或圆形 |
| thumbHeight                 | thumbBar的高度，不设置，默认为当前TextSeekBar的高度          |
| thumbType                   | thumbBar形状类型，当前支持square 矩形 和round 圆形           |
| progress                    | 设置默认进度                                                 |



#### 3.方法介绍

- 进度回调

```kotlin
seekbar.addOnChangeListener(object : SeekBarViewOnChangeListener {
            override fun touch(percent: Float, eventType: Int) {
                when (eventType) {
                    UP -> { // 手指触发抬起
                    }
                    MOVE -> { // 进度变更 or 手指滑动
                    }
                    DOWN -> { // 手指暗笑
                    }
                }
            }
        })
```

- 更新文本和进度

```kotlin
seekbar.setPercent("float 类型进度，0~1") // 该方法的文本内容沿用上一次设置的，如果从来没设置过，则显示空

seekbar.setPercent("float 类型进度，0~1",”需要显示的内容“)
```

- 禁用与启用

```kotlin
seekbar.isEnable = true  // 是否禁用，如果 为 true 禁用， false 不禁用，默认 false
```

