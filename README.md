### TextSeekBar

可以在seekBar的拖动条上添加文字的自定义view。
如果当前已有功能不能满足需求，源码已上传，希望可以给你的自定义提供一些参考。

##### 当前gradle版本是6.5的，如果因为该版本无法进行依赖的，建议copy代码食用，代码量也不多。

##### 如果是第一次接触自定义view的可以看看针对thumb[绘制的详细说明](https://github.com/xintanggithub/TextSeekBar/blob/master/DETAIL.md)。

#### 项目引入

- 1.根目录build.gradle添加
```
    allprojects {
        repositories {
            google()
            jcenter()
            // 添加下面这个
            maven{
                url 'https://raw.githubusercontent.com/xintanggithub/maven/master'
            }
            // 添加上面这个
        }
    }
```
- 2.在需要使用的module的build.gradle添加

```
    implementation 'com.tson.text.seek:1.0.1'
```


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
                    Up -> { // 手指触发抬起
                    }
                    Move -> { // 进度变更 or 手指滑动
                    }
                    Down -> { // 手指按下
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

