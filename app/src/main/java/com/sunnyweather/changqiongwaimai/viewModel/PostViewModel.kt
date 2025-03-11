package com.sunnyweather.changqiongwaimai.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sunnyweather.changqiongwaimai.data.model.Goods
import com.sunnyweather.changqiongwaimai.data.model.ResponseData
import com.sunnyweather.changqiongwaimai.data.repository.CategoryRepository
import kotlinx.coroutines.launch

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

}
