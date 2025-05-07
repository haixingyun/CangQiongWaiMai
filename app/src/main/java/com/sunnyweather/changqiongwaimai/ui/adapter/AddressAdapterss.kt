package com.sunnyweather.changqiongwaimai.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sunnyweather.changqiongwaimai.R
import com.sunnyweather.changqiongwaimai.data.model.Address

class AddressAdapterss(
    private val context: Context,
    private var addressList: List<Address>,
    private val defaultAddress: (Int) -> Unit,
    private val editAddress: (Int) -> Unit
) : RecyclerView.Adapter<AddressAdapterss.AddressViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_address, parent, false)
        return AddressViewHolder(view)
    }

    override fun getItemCount(): Int {
        return addressList.size
    }

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        val address = addressList[position]

        holder.title.text = when (address.label) {
            "1" -> "公司"
            "2" -> "家"
            "3" -> "学校"
            else -> "其他"
        }

        holder.address.text = "${address.provinceName}${address.cityName}${address.districtName}"
        holder.gender.text = when (address.sex) {
            "0" -> "男士"
            "1" -> "女士"
            else -> "未知"
        }
        holder.phone.text = address.phone

        when (address.isDefault) {
            0 -> holder.defaultAddress.isChecked = false
            1 -> holder.defaultAddress.isChecked = true
        }
        //选中默认设置为默认地址
        holder.defaultAddress.setOnClickListener {
            defaultAddress(address.id)  //回调方法
        }
        holder.ivEdit.setOnClickListener {
            editAddress(address.id)
        }
        holder.name.text = address.consignee
    }

    //更新数据
    fun updateData(newData: List<Address>) {
        this.addressList = newData
        //这个方法是 RecyclerView Adapter 的方法，作用是：
        //告诉 RecyclerView 整个数据列表已经变化了，请重新绑定所有 item。
        notifyDataSetChanged()
    }

    class AddressViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title = itemView.findViewById<TextView>(R.id.title)
        val address = itemView.findViewById<TextView>(R.id.address)
        val name = itemView.findViewById<TextView>(R.id.name)
        val gender = itemView.findViewById<TextView>(R.id.gender)
        val phone = itemView.findViewById<TextView>(R.id.phone)
        val defaultAddress = itemView.findViewById<CheckBox>(R.id.set_default_address)
        val ivEdit = itemView.findViewById<ImageView>(R.id.iv_edit)
    }
}