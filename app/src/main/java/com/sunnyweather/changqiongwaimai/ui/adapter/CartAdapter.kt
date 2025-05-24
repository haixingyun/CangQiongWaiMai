package com.sunnyweather.changqiongwaimai.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sunnyweather.changqiongwaimai.R
import com.sunnyweather.changqiongwaimai.data.model.Dish

class CartAdapter(
    private val context: Context,
    private val cartList: List<Dish>,
    private val onIncrease: (Int) -> Unit,
    private val onDecrease: (Int) -> Unit,
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_goods, parent, false)
        return CartViewHolder(view)
    }

    override fun getItemCount(): Int {
        return cartList.size
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val goods = cartList[position]

        Glide.with(context)
            .load(goods.image)
            .error(R.drawable.loding_err)
            .into(holder.goodsImage)

        holder.goodsName.text = goods.name
        holder.goodsPrice.text = goods.amount.toString()
        holder.goodsNumber.text = goods.number.toString()
        holder.YuanLiao.text = goods.dishFlavor ?: ""
        holder.XiaoLiag.visibility = View.INVISIBLE
        //点击减少事件
        holder.goodsDecrease.setOnClickListener {
            onDecrease(goods.dishId)
        }
        //点击添加事件
        holder.goodsAdd.setOnClickListener {
            onIncrease(goods.dishId)
        }
    }

    class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val goodsImage: ImageView = itemView.findViewById(R.id.goods_image)
        val goodsName: TextView = itemView.findViewById(R.id.goods_name)
        val goodsPrice: TextView = itemView.findViewById(R.id.goods_price)
        val goodsNumber: TextView = itemView.findViewById(R.id.goods_number)
        val goodsDecrease: TextView = itemView.findViewById(R.id.tv_subtract)
        val goodsAdd: TextView = itemView.findViewById(R.id.tv_add)
        val XiaoLiag: LinearLayout = itemView.findViewById(R.id.goods_xiaoLiang)
        val YuanLiao: TextView = itemView.findViewById(R.id.yuanLiao)
    }



}
