package com.sunnyweather.changqiongwaimai.utils

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.sunnyweather.changqiongwaimai.ui.fragment.RecyclerFragment

class ViewPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    private val titles = listOf("全部订单", "待付款", "已取消")
    private val fragments = listOf(
        RecyclerFragment.newInstance("全部订单"),
        RecyclerFragment.newInstance("待付款"),
        RecyclerFragment.newInstance("已取消")
    )

    override fun getItem(position: Int): Fragment = fragments[position]

    override fun getCount(): Int = fragments.size

    override fun getPageTitle(position: Int): CharSequence = titles[position]
}
