package com.sunnyweather.changqiongwaimai.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sunnyweather.changqiongwaimai.R
import com.sunnyweather.changqiongwaimai.data.model.GlobalData
import com.sunnyweather.changqiongwaimai.data.model.MenuItem

class MenuAdapter(
    private val context: Context,
    private val menuList: List<MenuItem>
) : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

    // 使用 lambda 回调，只传递菜单项的 id（你也可以传递整个 MenuItem 对象）
    var onItemClick: ((id: Int) -> Unit)? = null

    // 当前选中的项索引
    private var selectedPosition = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_menu, parent, false)
        return MenuViewHolder(view)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val menuItem = menuList[position]
        holder.menuItemTextView.text = menuItem.name

        // 根据是否为选中项设置背景
        val backgroundRes = if (position == selectedPosition)
            R.color.menu_item_selected_background
        else
            R.color.menu_item_normal_background
        holder.itemView.setBackgroundResource(backgroundRes)

        // 设置点击事件
        holder.itemView.setOnClickListener {


            //存上一个位置
            val previousPosition = selectedPosition
            selectedPosition = holder.adapterPosition  //为选中位置赋最新的值

            // 更新选中项：先刷新旧的，再刷新新的
            notifyItemChanged(previousPosition)
            notifyItemChanged(selectedPosition)

            //将分类id存入全局单例
            GlobalData.id = menuItem.categoryId

            // 通过 lambda 回调将菜单项的 id 传给 Activity
            onItemClick?.invoke(menuItem.categoryId)
        }
    }

    override fun getItemCount(): Int = menuList.size

    class MenuViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val menuItemTextView: TextView = itemView.findViewById(R.id.menu_item_text)
    }
}
