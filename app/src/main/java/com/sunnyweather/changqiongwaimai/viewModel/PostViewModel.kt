package com.sunnyweather.changqiongwaimai.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sunnyweather.changqiongwaimai.data.model.Goods
import com.sunnyweather.changqiongwaimai.data.model.ResponseData
import com.sunnyweather.changqiongwaimai.data.repository.CategoryRepository

class PostViewModel : BaseViewModel() {

    private val categoryRepository = CategoryRepository()  // 依赖 Repository

    private val _posts = MutableLiveData<ResponseData<List<Goods>>>()
    val posts: LiveData<ResponseData<List<Goods>>> = _posts

    /**
     * 根据分类id查询菜品
     */
    fun fetchPosts(id: Int) {
        handleNetworkException {
            val result = categoryRepository.categoryIdGetDish(id)
            _posts.value = result
        }
    }

    /**
     * 加数量
     */
    fun incrementQuantity(goods: Goods) {
        // 拿到当前的 ResponseData<List<Goods>>
        val currentResponse = _posts.value ?: return
        // 从中再拿到具体的商品列表
        val currentList = currentResponse.data ?: return
        // 对商品列表进行 map 操作
        val newGoodsList = currentList.map {
            if (it.id == goods.id) {
                it.copy(quantity = it.quantity + 1)
            } else {
                it
            }
        }
        _posts.value = currentResponse.copy(data = newGoodsList)
    }

    /**
     * 减数量
     */
    fun decrementQuantity(goods: Goods) {
        val currentResponse = _posts.value ?: return
        val currentList = currentResponse.data ?: return

        val newGoodsList = currentList.map {
            if (it.id == goods.id) {
                // 保证数量不减为负数
                it.copy(quantity = maxOf(0, it.quantity - 1))
            } else {
                it
            }
        }
        _posts.value = currentResponse.copy(data = newGoodsList)
    }
}
