package com.sunnyweather.changqiongwaimai.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sunnyweather.changqiongwaimai.data.model.Goods
import com.sunnyweather.changqiongwaimai.data.repository.CategoryRepository
import kotlinx.coroutines.launch

class PostViewModel(
) : BaseViewModel() {

    private val cartViewModel =  CartViewModel()
    private val categoryRepository = CategoryRepository()

    // 1. 原始商品列表，MutableLiveData
    private val _rawGoods = MutableLiveData<List<Goods>>(emptyList())

    // 2. 把 CartRepository 的 StateFlow 转成 LiveData
    //    需要导入：import androidx.lifecycle.asLiveData
    private val cartMapLiveData: LiveData<Map<Int, Int>> =  cartViewModel.cartMap

    // 3. 用 MediatorLiveData 来合并两路 LiveData，生成 UI 层要用的列表
    val uiList: MediatorLiveData<List<Goods>> = MediatorLiveData<List<Goods>>().apply {
        // whenever 原始商品列表变化，或购物车数量变化，都重新计算一次
        fun update() {
            val goods    = _rawGoods.value ?: return
            val cartMap  = cartMapLiveData.value ?: emptyMap()
            value = goods.map { item ->
                // copy 出一个带最新 quantity 的副本
                item.copy(quantity = cartMap[item.id] ?: 0)
            }
        }

        addSource(_rawGoods)     { update() }
        addSource(cartMapLiveData) { update() }
    }

    /** 拉取分类下商品 */
    fun fetchPosts(categoryId: Int) {
        viewModelScope.launch {
            handleNetworkException {
                val result = categoryRepository.categoryIdGetDish(categoryId)
                // 更新原始列表
                _rawGoods.postValue(result.data.orEmpty())
            }
        }
    }
}
