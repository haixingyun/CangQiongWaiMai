package com.sunnyweather.changqiongwaimai.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sunnyweather.changqiongwaimai.data.repository.CategoryRepository
import com.sunnyweather.changqiongwaimai.viewModel.CartViewModel
import com.sunnyweather.changqiongwaimai.viewModel.PostViewModel

/**
 * 为了方便传入cartViewModel统一实例
 */
class PostViewModelFactory(
    private val cartViewModel: CartViewModel,
    private val categoryRepository: CategoryRepository = CategoryRepository()
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PostViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PostViewModel(cartViewModel,categoryRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
