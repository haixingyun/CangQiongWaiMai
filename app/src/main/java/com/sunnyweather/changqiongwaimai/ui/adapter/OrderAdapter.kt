package com.sunnyweather.changqiongwaimai.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sunnyweather.changqiongwaimai.R
import com.sunnyweather.changqiongwaimai.data.model.Order
import com.sunnyweather.changqiongwaimai.data.model.OrderDetail

class OrderAdapters(
    private val content: Context,
    private val orders: List<Order>,
    private val zaiLaiYiDan: (Int) -> Unit,
    private val orderDetail: (Int) -> Unit, // 这里是传递回调函数
    private val CuiDan: (Int) -> Unit,  //催单按钮回调
    private val goPay:(Int) -> Unit  //去支付按钮回调

) : RecyclerView.Adapter<OrderAdapters.OrderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun getItemCount(): Int {
        return orders.size
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        holder.bind(order.orderDetailList, orderDetail) // 传递 orderDetail 回调
        holder.xiaDanTime.text = order.orderTime
        holder.orderStart.text = when (order.status) {
            1 -> "待付款"
            2 -> "待接单"
            3 -> "已结单"
            4 -> "派送中"
            5 -> "已完成"
            6 -> "已取消"
            7 -> "退款"
            else -> "未知状态"
        }
        //如果状态为待接单催单控件变为可见
        if (order.status == 2) {
            holder.CuiDan.visibility = View.VISIBLE
            //事件 监听催单
            holder.CuiDan.setOnClickListener {
                CuiDan(order.id)
            }
        }else if (order.status == 1) {
            holder.CuiDan.visibility = View.VISIBLE
            holder.CuiDan.text = "去支付"
            holder.CuiDan.setOnClickListener {
                goPay(order.id)
            }
        }

        holder.totalNumber.text = "共${order.packAmount}件"
        holder.totalPrice.text = "￥${"%.2f".format(order.amount.toDouble())}"
        //事件：再来一单
        holder.zaiLaiYiDan.setOnClickListener {
            zaiLaiYiDan(order.id)
        }
    }

    class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val orderGoodRecyclerView: RecyclerView =
            itemView.findViewById(R.id.orderRecyclerView)  // 子RecyclerView
        val xiaDanTime: TextView = itemView.findViewById(R.id.xia_dan_shi_jian)
        val orderStart: TextView = itemView.findViewById(R.id.orderStart)
        val totalPrice: TextView = itemView.findViewById(R.id.totalPrice)
        val totalNumber: TextView = itemView.findViewById(R.id.totalNumber)
        val zaiLaiYiDan: TextView = itemView.findViewById(R.id.zaiLaiYiDan)   // 再来一单按钮
        val CuiDan: TextView = itemView.findViewById(R.id.ChuiDan)

        // 初始化内部adapter
        fun bind(Dishs: List<OrderDetail>, orderDetail: (Int) -> Unit) {
            val DishsAdapter = OrderDishAdapter(itemView.context, Dishs) { orderId ->
                orderDetail(orderId) // 或者根据需要传递其他数据
            }
            // 设置内层 RecyclerView 为水平排列
            orderGoodRecyclerView.layoutManager =
                LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
            orderGoodRecyclerView.adapter = DishsAdapter
        }
    }
}
