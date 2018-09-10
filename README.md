### Nice to meet you
## 项目说明
* 这是一个基于Android Wear创建的项目结构
* 包含一个用于圆形表盘的自定义view
* 包含一个DialogFragment在wear上的兼容抽象类
* 包含一些Wear开发注意事项

### 注意事项
    Enables Always-on （国内手表rom activity可能无法继承WearableActivity需要disable Ambient，可继承如AppCompatActivity／FragmentActivity）
    国内ROM厂商定制wear系统时存在差异，可能导致某些wear相关功能被阉割或源码版本不支持（调用时会报异常）
    或者wearable包下控件无法使用（如BoxInsideLayout使用时无法展示特性，表现和普通viewGroup效果一样）
    更多wear相关信息 请查看 google developer --  https://developer.android.google.cn/training/building-wearables
    Wear的UI设计和交互可参考国内目前做的比较好的--出门问问公司相关产品（不是广告哈）
