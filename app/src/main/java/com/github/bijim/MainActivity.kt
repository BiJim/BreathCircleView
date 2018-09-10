package com.github.bijim

import android.os.Bundle
import android.support.wearable.activity.WearableActivity
import com.github.bijim.breathcircleview.R

class MainActivity : WearableActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Enables Always-on （国内手表rom activity可能无法继承WearableActivity需要disable Ambient，可继承如AppCompatActivity／FragmentActivity）
//        setAmbientEnabled()
        // 国内ROM厂商定制wear系统时存在差异，可能导致某些wear相关功能被阉割或源码版本不支持（调用时会报异常）
        // 或者wearable包下控件无法使用（如BoxInsideLayout使用时无法展示特性，表现和普通viewGroup效果一样）
        // 更多wear相关信息 请查看 google developer --  https://developer.android.google.cn/training/building-wearables
    }
}
