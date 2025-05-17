package com.sunnyweather.changqiongwaimai.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.sunnyweather.changqiongwaimai.base.BaseViewModel
import com.sunnyweather.changqiongwaimai.data.model.Goods
import com.sunnyweather.changqiongwaimai.data.repository.CategoryRepository

class PostViewModel(
    private val cartViewModel: CartViewModel,
    private val categoryRepository: CategoryRepository
) : BaseViewModel() {


    // 1. 原始商品列表，MutableLiveData
    private val _rawGoods = MutableLiveData<List<Goods>>(emptyList())

    // 直接使用注入的 cartViewModel 的 cartMap
    private val cartMapLiveData: LiveData<Map<Int, Int>> = cartViewModel.cartMap

    // 3. 用 MediatorLiveData 来合并两路 LiveData，生成 UI 层要用的列表
    val uiList: MediatorLiveData<List<Goods>> = MediatorLiveData<List<Goods>>().apply {
        // whenever 原始商品列表变化，或购物车数量变化，都重新计算一次
        fun update() {
            val goods = _rawGoods.value ?: return
            val cartMap = cartMapLiveData.value ?: emptyMap()
            Log.d("ViewModel","cartMap:$cartMap")
            value = goods.map { item ->
                // copy 出一个带最新 quantity 的副本
                item.copy(quantity = cartMap[item.id] ?: 0)
            }
        }

        addSource(_rawGoods) { update() }
        addSource(cartMapLiveData) { update() }
    }

    /**
     *  获取分类商品数据
     *  @param categoryId 分类id
     */
    fun fetchPosts(categoryId: Int) {
        request(request = { categoryRepository.categoryIdGetDish(categoryId) },
            onSuccess = { data -> _rawGoods.postValue(data.orEmpty()) })
    }

    //    data.orEmpty()
    //    orEmpty() 是 Kotlin 的一个扩展函数，用于防止空指针异常：
    //    如果 data 是 null，则返回空集合；
    //    如果 data 不是 null，就原样返回。
}
