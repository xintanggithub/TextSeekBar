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
    implementation 'com.tson.text.seek:1.0.4'
```


#### 示例

![示例](https://github.com/xintanggithub/TextSeekBar/blob/master/pic.png?raw=true)

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

| 自定义属性                    | 类型            | 说明                                                         |
| --------------------------- | ------------------------------------------------------------ | --------------------------- |
| thumbHide | boolean | thumb是否隐藏，默认false，不隐藏 |
| touchEnable | boolean | 是否禁用手指拖动，true 禁用， false默认值，不禁用 |
| thumbText | string | thumb文字内容 |
| thumbTextSize | dimension | thumb字体大小 |
| thumbTextColor | color/referencet | humb字体颜色 |
| thumbBorderWidth | dimension | thumb边框宽度 |
| thumbBorderColor | color/reference | thumb边框颜色 |
| thumbBorderStartColor | color/reference | thumb边框渐变开始颜色 |
| thumbBorderEndColor | color/reference | thumb边框渐变结束颜色 |
| headEndPadding | dimension | thumb前后padding值，可以为负 |
| prospectProgressBarHeight | dimension | 前景进度条高度 |
| prospectProgressBarStartColor | color/reference | 前景进度条开始的渐变颜色 |
| prospectProgressBarColor | color/reference | 前景进度条颜色 |
| prospectProgressBarEndColor | color/reference | 前景进度条结束的渐变颜色 |
| prospectProgressBarOffset | dimension | 触发偏移量 |
| backgroundProgressBarHeight | dimension | 背景进度条高度 |
| backgroundProgressBarStartColor | color/reference | 背景进度条渐变开始颜色 |
| backgroundProgressBarColor | color/reference | 背景进度条颜色（如果设置为渐变，则，这个是中间的） |
| backgroundProgressBarEndColor | color/reference | 背景进度条渐变结束颜色 |
| backgroundProgressBarOffset | dimension | 触发偏移量 |
| thumbBackgroundStartColor | color/reference | thumb开始颜色 |
| thumbBackgroundColor | color/reference | thumb颜色 |
| thumbBackgroundEndColor | color/reference | thumb结束颜色 |
| thumbOffset | dimension | thumb偏移量 |
| thumbWidth | dimension | thumb宽度 |
| thumbHeight | dimension | thumb高度 |
| thumbType | square、round | thumb圆角还是矩形 |
| strokeCap | butt、round、square | 画笔类型，butt：和square一样是直角画笔，但是不会充满开始和结束，round：圆角，square：直角画笔，可以充满前后的距离 |
| progress | integer | thumb进度 |
| thumbIcon | color/reference  | thumb图标 |
| thumbIconWidth | dimension  | thumb图标宽，默认为icon的原始大小，超出边界将会被裁剪 |
| thumbIconHeight | dimension  | thumb图标高，默认为icon的原始大小，超出边界将会被裁剪 |

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
seekbar.isEnable = true  // 是否禁用，如果 为 true 禁用，seekbar不可拖动， false 不禁用，seekbar可拖动，默认 false
```

