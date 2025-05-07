package com.sunnyweather.changqiongwaimai

import android.app.Application
import com.hjq.toast.Toaster

/**
 *  author : 小海
 *  desc   : Application初始化
 *  time   : 2025/5/7 22:09
 */
class App : Application() {
    override fun onCreate() {
        super.onCreate()

        // 初始化 Toast 框架
        Toaster.init(this);
    }
}