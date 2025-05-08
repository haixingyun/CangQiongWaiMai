package com.sunnyweather.changqiongwaimai.ui.fragment

import android.content.Context
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sunnyweather.changqiongwaimai.R
import com.sunnyweather.changqiongwaimai.data.model.City
import com.sunnyweather.changqiongwaimai.data.model.Province
import com.sunnyweather.changqiongwaimai.ui.adapter.AddressAdapter

/**
 * 地址底部弹出框
 */
class AddressPickerBottomSheetDialog(
    private val context: Context,
    private val onSelected: (String) -> Unit
) : BottomSheetDialog(context) {

    private var level = 0
    private var selectedProvince: Province? = null
    private var selectedCity: City? = null
    private var provinces: List<Province> = emptyList()

    // ✅ 用 lateinit 声明 adapter
    private lateinit var adapter: AddressAdapter

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_address_picker, null)
        setContentView(view)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // ✅ 初始化 adapter，并设置点击逻辑
        adapter = AddressAdapter { name ->
            when (level) {
                0 -> {
                    selectedProvince = provinces.find { it.name == name }
                    level = 1
                    adapter.update(selectedProvince?.city?.map { it.name } ?: emptyList())
                }
                1 -> {
                    selectedCity = selectedProvince?.city?.find { it.name == name }
                    level = 2
                    adapter.update(selectedCity?.area ?: emptyList())
                }
                2 -> {
                    val result = "${selectedProvince?.name} ${selectedCity?.name} $name"
                    onSelected(result)
                    dismiss()
                }
            }
        }

        recyclerView.adapter = adapter

        loadJson()
    }

    private fun loadJson() {
        val json = context.assets.open("province.json").bufferedReader().use { it.readText() }
        provinces = Gson().fromJson(json, object : TypeToken<List<Province>>() {}.type)
        adapter.update(provinces.map { it.name })
    }
}
