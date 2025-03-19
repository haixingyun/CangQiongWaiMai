package com.sunnyweather.changqiongwaimai.ui.adapter

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sunnyweather.changqiongwaimai.R
import com.sunnyweather.changqiongwaimai.data.model.Goods
import com.sunnyweather.changqiongwaimai.viewModel.PostViewModel

class GoodsAdapter(
    private val context: Context,
    private var goodsList: List<Goods>,
    // 将回调接口改为 lambda 表达式：传入两个参数，分别为商品ID和选中的辣度
    private val onAddCartClicked: (goodsId: Int, selectedSpicyLevel: String) -> Unit,
    private val onIncrease: (Goods) -> Unit,    //商品增加回调
    private val onDecrease: (Goods) -> Unit    //商品减少回调
) :
    RecyclerView.Adapter<GoodsAdapter.GoodsViewHolder>() {

    private var selectedSpicyLevel: String = ""


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoodsViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_goods, parent, false)
        return GoodsViewHolder(view)
    }

    override fun onBindViewHolder(holder: GoodsViewHolder, position: Int) {
        val goods = goodsList[position]
        val id = goods.id

        holder.YuanLiao.text = goods.description  //原料
        holder.goodsNameTextView.text = goods.name  //回显商品名字
        holder.goodsPriceTextView.text = "￥${goods.price}"
        holder.goodsNumber.text = goods.quantity.toString()
        //如果口味为空加载加减视图  不为空反之
        if (goods.flavors.isEmpty()) {
            holder.GuiGe.visibility = View.GONE
            holder.JiaJian.visibility = View.VISIBLE
        } else {
            holder.GuiGe.visibility = View.VISIBLE
            holder.JiaJian.visibility = View.GONE
        }
        //如果商品数量为0隐藏减视图
        if (goods.quantity==0){
            holder.goodsSubtract.visibility = View.GONE
            holder.goodsNumber.visibility = View.GONE
        }else{
            holder.goodsSubtract.visibility = View.VISIBLE
            holder.goodsNumber.visibility = View.VISIBLE
        }
        //加载图片
        Glide.with(context)
            .load(goods.image)
            .into(holder.goodsImageView)

        // 设置 选择规格 点击事件，点击后弹出自定义选择规格BottomSheetDialog
        holder.GuiGe.setOnClickListener {
            showCenterDialog(goods.price, id)
        }
        //设置添加商品数量点击事件
        holder.goodsAdd.setOnClickListener {
            onIncrease(goods)
        }
        //设置减少商品数量点击事件
        holder.goodsSubtract.setOnClickListener {
            onDecrease(goods)
        }

    }

    override fun getItemCount(): Int {
        return goodsList.size
    }

    class GoodsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val goodsImageView: ImageView = itemView.findViewById(R.id.goods_image)

        val YuanLiao: TextView = itemView.findViewById(R.id.yuanLiao)  //原料
        val goodsNameTextView: TextView = itemView.findViewById(R.id.goods_name)  //商品名字
        val goodsPriceTextView: TextView = itemView.findViewById(R.id.goods_price) //商品价格
        val goodsNumber: TextView = itemView.findViewById(R.id.goods_number)  //商品数量
        //加减layout
        val JiaJian: LinearLayout = itemView.findViewById(R.id.jiaJian)
        //获取按钮
        val GuiGe: TextView = itemView.findViewById(R.id.goods_guiGe)
        val goodsAdd: TextView = itemView.findViewById(R.id.tv_add)   //添加数量
        val goodsSubtract: TextView = itemView.findViewById(R.id.tv_subtract)  //减少数量

    }

    // 添加更新数据的方法
    fun submitList(newList: List<Goods>) {
        goodsList = newList
        notifyDataSetChanged()
    }




    private fun showCenterDialog(price: Double, id: Int) {
        // 创建 Dialog 实例
        val dialog = Dialog(context)
        // 设置自定义布局
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_select_spicy, null)
        dialog.setContentView(view)

        // 修改窗口属性：设置宽度、高度以及居中显示
        dialog.window?.apply {
            // 设置背景为透明，避免默认背景占位
            setBackgroundDrawableResource(android.R.color.transparent)
            // 获取当前窗口的属性
            val params = attributes
            // 设置宽度和高度（例如：屏幕宽度的80%，高度自适应内容）
            params.width = (context.resources.displayMetrics.widthPixels * 0.8).toInt()
            // 如果需要固定高度，也可以直接赋值，例如：params.height = 600
            // 设置居中显示
            params.gravity = android.view.Gravity.CENTER
            attributes = params
        }

        dialog.show()

        val spicyRadioGroup = view.findViewById<RadioGroup>(R.id.spicy_radio_group)

        val goodsPrice = view.findViewById<TextView>(R.id.goods_price)  //获取控件
        goodsPrice.text = price.toInt().toString()  //商品价格赋值


        spicyRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            selectedSpicyLevel = when (checkedId) {
                R.id.rb_no_spicy -> "不辣"
                R.id.rb_little_spicy -> "微辣"
                R.id.rb_medium_spicy -> "中辣"
                R.id.rb_high_spicy -> "重辣"
                else -> "不辣"
            }
        }

        //事件 监听定义选择规格BottomSheetDialog加入到购物车按钮
        view.findViewById<TextView>(R.id.cart_add).setOnClickListener {
            // 调用 lambda 回调
            onAddCartClicked(id, selectedSpicyLevel)
            dialog.dismiss()
        }

    }

    // 模拟一个显示 Toast 的方法（你可以替换为你实际的 Toast 实现）
    private fun showToast(message: String) {
        // 这里需要引入 android.widget.Toast
        android.widget.Toast.makeText(context, message, android.widget.Toast.LENGTH_SHORT)
            .show()
    }
}