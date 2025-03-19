package com.sunnyweather.changqiongwaimai.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sunnyweather.changqiongwaimai.R
import com.sunnyweather.changqiongwaimai.data.model.OrderDetail

class OrderDishAdapter(
    private val context: Context,
    private val Dishs: List<OrderDetail>,
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<OrderDishAdapter.OrderDishViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderDishViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_order_goods, parent, false)
        return OrderDishViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderDishViewHolder, position: Int) {
        val dish = Dishs[position]

        Glide.with(context)
            .load(dish.image)
            .error(R.drawable.loding_err)
            .into(holder.dishImg)

        holder.dishName.text = dish.name

        holder.itemView.setOnClickListener {
            onItemClick(dish.orderId) // 调用点击回调，传递OrderDetail对象
        }
    }

    override fun getItemCount(): Int {
        return Dishs.size
    }

    class OrderDishViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dishImg: ImageView = itemView.findViewById(R.id.dishImg)
        val dishName: TextView = itemView.findViewById(R.id.dishName)
    }
}