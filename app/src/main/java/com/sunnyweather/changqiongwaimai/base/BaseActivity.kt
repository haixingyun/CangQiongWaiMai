package com.sunnyweather.changqiongwaimai.base

import android.os.Bundle
import android.os.PersistableBundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.hjq.toast.Toaster

/**
 *  author : 小海
 *  desc   : Activity父类
 *  time   : 2025/5/8 16:58
 */
open class BaseActivity : AppCompatActivity() {

    private val baseViewModel: BaseViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        baseViewModel.error.observe(this) { error ->
            Toaster.showLong(error)
        }
    }
}