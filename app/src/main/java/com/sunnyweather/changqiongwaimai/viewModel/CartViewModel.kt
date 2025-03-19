package com.sunnyweather.changqiongwaimai.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sunnyweather.changqiongwaimai.data.model.Dish
import com.sunnyweather.changqiongwaimai.data.repository.CartRepository
import kotlinx.coroutines.launch

class CartViewModel : ViewModel() {
    private val repository = CartRepository()  // 依赖 Repository

    private val _posts = MutableLiveData<List<Dish>?>()
    val posts: MutableLiveData<List<Dish>?> = _posts

    /**
     * 查询购物车
     */
    fun getCart() {
        viewModelScope.launch {
            val response = repository.getCart()
            Log.d("CartNetwork","购物车的数据：$response")
            if (response.code == 1)  _posts.value = response.data
        }
    }


}

