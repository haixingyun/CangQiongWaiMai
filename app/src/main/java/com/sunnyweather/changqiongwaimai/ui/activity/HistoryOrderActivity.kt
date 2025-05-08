package com.sunnyweather.changqiongwaimai.ui.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.sunnyweather.changqiongwaimai.R
import com.sunnyweather.changqiongwaimai.base.BaseActivity
import com.sunnyweather.changqiongwaimai.ui.adapter.ViewPagerAdapter

class HistoryOrderActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.order_history_activity)

        val tabLayout: TabLayout = findViewById(R.id.tabLayout)
        val viewPager: ViewPager2 = findViewById(R.id.viewPager)

        val adapter = ViewPagerAdapter(this)
        viewPager.adapter = adapter
        // 关联TabLayout
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "全部订单"
                1 -> "待付款"
                2 -> "已取消"
                else -> ""
            }
        }.attach()

        // 禁用预加载（按需）
        viewPager.offscreenPageLimit = 1 // 0会有兼容性问题
    }
}