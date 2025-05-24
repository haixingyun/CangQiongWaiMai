package com.sunnyweather.changqiongwaimai.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sunnyweather.changqiongwaimai.base.BaseViewModel
import com.sunnyweather.changqiongwaimai.data.model.Dish
import com.sunnyweather.changqiongwaimai.data.model.Flavor
import com.sunnyweather.changqiongwaimai.data.repository.CartRepository

class CartViewModel : BaseViewModel() {

    private val repository = CartRepository()      //购物车 Repository

    private val _posts = MutableLiveData<List<Dish>>()      // 原本用于展示 Cart 列表的数据
    val posts: LiveData<List<Dish>> = _posts

    private val _cartMap = MutableLiveData<Map<Int, Int>>(emptyMap())    // 用一个 MutableLiveData 来保存：Map<商品Id, 数量>
    val cartMap: LiveData<Map<Int, Int>> = _cartMap

    /** 查询购物车 */
    fun getCart() {
        request(request = { repository.getCart() }, onSuccess = { data ->
            _posts.postValue(data!!)
            _cartMap.value =data.associate { it.dishId to it.number }  //associate：扩展函数，常用于将集合转换为 Map 类型。
            Log.d("ViewModel", "_cartMap.value = ${_cartMap.value}")  // 这里肯定能马上看到
        })
    }

    /**
     * 将商品添加到购物车
     * @param goodsId 商品id
     */
    fun addToCart(
        goodsId: Int
    ) {
        request(
            request = { repository.addToCart(Flavor(goodsId)) },
            onSuccess = { _ -> getCart() }
        )
    }

    /**
     *  商品取消购物车
     *  @param goodsId 商品id
     */
    fun cartSubtractGoods(
        goodsId: Int
    ) {
        request(
            request = { repository.cartSubtractGoods(Flavor(goodsId)) },
            onSuccess = { _ -> getCart() }
        )
    }
}
