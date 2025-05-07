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

class OrderDetailAdapter(
    private val content: Context,
    private val orderDetailList: List<OrderDetail>
) : RecyclerView.Adapter<OrderDetailAdapter.OrderViewAdapter>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OrderDetailAdapter.OrderViewAdapter {
        val view = LayoutInflater.from(content).inflate(R.layout.item_order_detail, parent, false)
        return OrderDetailAdapter.OrderViewAdapter(view)
    }

    override fun getItemCount(): Int {
        return orderDetailList.size
    }

    override fun onBindViewHolder(holder: OrderViewAdapter, position: Int) {
        val orderDetail = orderDetailList[position]
        holder.goodsName.text = orderDetail.name
        Glide.with(content)
            .load(orderDetail.image)
            .error(R.drawable.loding_err)
            .into(holder.orderDetailImg)

        holder.goodsNumber.text = "x ${orderDetail.number}"

        holder.goodsTotalPrice.text = "￥${"%.2f".format(orderDetail.amount)}"

    }

    class OrderViewAdapter(itemView: View) : RecyclerView.ViewHolder(itemView) {
         val orderDetailImg = itemView.findViewById<ImageView>(R.id.order_detail_img)
         val goodsName = itemView.findViewById<TextView>(R.id.tv_good_name)
         val goodsNumber = itemView.findViewById<TextView>(R.id.goods_number)
         val goodsTotalPrice = itemView.findViewById<TextView>(R.id.goods_total_price)
    }
}