package com.sunnyweather.changqiongwaimai.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.sunnyweather.changqiongwaimai.ui.fragment.RecyclerFragment

class ViewPagerAdapter(fm: FragmentActivity) : FragmentStateAdapter(fm) {

    private val titles = listOf("全部订单", "待付款", "已取消")
    private val statusTypes = listOf("全部订单", "待付款", "已取消") // 或者用 Enum/Int

    override fun getItemCount(): Int {
        return titles.size
    }

    override fun createFragment(position: Int): Fragment {
        // 根据位置创建对应状态的 Fragment 实例
        return RecyclerFragment.newInstance(statusTypes[position])
    }
    
}
