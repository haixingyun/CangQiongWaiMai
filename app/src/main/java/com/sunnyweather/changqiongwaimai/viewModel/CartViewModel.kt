package com.sunnyweather.changqiongwaimai.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sunnyweather.changqiongwaimai.data.model.Dish
import com.sunnyweather.changqiongwaimai.data.model.Flavor
import com.sunnyweather.changqiongwaimai.data.repository.CartRepository
import kotlinx.coroutines.launch

class CartViewModel : ViewModel() {
    private val repository = CartRepository()  // 依赖 Repository

    // 原本用于展示 Cart 列表的数据
    private val _posts = MutableLiveData<List<Dish>>()
    val posts: LiveData<List<Dish>> = _posts

    // ▼—— 全部 LiveData ——▼
    // 用一个 MutableLiveData 来保存：Map<商品Id, 数量>
    private val _cartMap = MutableLiveData<Map<Int, Int>>(emptyMap())
    val cartMap: LiveData<Map<Int, Int>> = _cartMap

    /** 查询购物车 */
    fun getCart() {
        viewModelScope.launch {
            val response = repository.getCart()
            if (response.code == 1) {
                // 更新列表和数量映射
                _posts.postValue(response.data!!)
                _cartMap.postValue(
                    response.data.associate { it.dishId to it.number }
                )
            }
        }
    }

    /** 添加到购物车 */
    fun addToCart(
        goodsId: Int,
        onSuccess: () -> Unit = {},
        onError: (Throwable) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                repository.addToCart(Flavor(goodsId))
                // 同步更新本地的数量映射
                val current = _cartMap.value.orEmpty().toMutableMap()
                current[goodsId] = (current[goodsId] ?: 0) + 1
                _cartMap.postValue(current)
                onSuccess()
            } catch (e: Exception) {
                onError(e)
            }
        }
    }

    /** 从购物车减少 */
    fun cartSubtractGoods(
        goodsId: Int,
        onSuccess: () -> Unit = {},
        onError: (Throwable) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                repository.cartSubtractGoods(Flavor(goodsId))
                onSuccess()
                // 同步更新本地的数量映射
                val current = _cartMap.value.orEmpty().toMutableMap()
                val newCount = (current[goodsId] ?: 0) - 1
                if (newCount > 0) {
                    current[goodsId] = newCount
                } else {
                    current.remove(goodsId)
                }
                _cartMap.postValue(current)
            } catch (e: Exception) {
                onError(e)
            }
        }
    }
}
