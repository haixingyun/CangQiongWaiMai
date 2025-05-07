package com.sunnyweather.changqiongwaimai.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/**
 * 地址选择器适配器
 */

class AddressAdapter(private val onItemClick: (String) -> Unit) :
    RecyclerView.Adapter<AddressAdapter.ViewHolder>() {

    private var data: List<String> = emptyList()

    fun update(newData: List<String>) {
        data = newData
        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val text: TextView = view.findViewById(android.R.id.text1)

        init {
            view.setOnClickListener {
                onItemClick(data[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_1, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.text.text = data[position]
    }
}
