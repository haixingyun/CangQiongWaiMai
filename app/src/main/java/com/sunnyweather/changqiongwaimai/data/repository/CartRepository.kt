package com.sunnyweather.changqiongwaimai.data.repository

import android.util.Log
import com.sunnyweather.changqiongwaimai.data.api.RetrofitClient
import com.sunnyweather.changqiongwaimai.data.model.CategoryDish
import com.sunnyweather.changqiongwaimai.data.model.Dish
import com.sunnyweather.changqiongwaimai.data.model.Flavor
import com.sunnyweather.changqiongwaimai.data.model.ResponseData

class CartRepository {

    /**
     * 将商品添加到购物车
     */
    suspend fun addToCart(flavor: Flavor): Boolean {
        return try {
            val response = RetrofitClient.apiService.addCartGood(flavor)
            if (response.code == 1) {
                Log.d("CartRepository", "购物车添加成功")
                true // 添加成功
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e("CartRepository", "购物车添加失败: ${e.message}")
            false // 添加失败
        }
    }

    /**
     * 购物车减少商品
     */
    suspend fun cartSubtractGoods(flavor: Flavor): ResponseData<*> {
        return safeApiCall {
            RetrofitClient.apiService.subtractCartGood(flavor)
        }
    }

    /**
     * 获取购物车的商品
     */
    suspend fun getCart(): ResponseData<List<Dish>> {
        return safeApiCall {
            Log.d("ApiCall","购物车数据")
            RetrofitClient.apiService.getCartGood()
        }
    }

    /**
     * 清空购物车
     */
    suspend fun emptyCart(): ResponseData<*> {
        return safeApiCall {
            Log.d("ApiCall","清空购物车")
            RetrofitClient.apiService.emptyCartGoods()
        }
    }


    /**
     * 查询分类菜品
     */
    suspend fun getCategoryDish(): ResponseData<CategoryDish> {
        return safeApiCall {
            Log.d("ApiCall","根据分类查询菜品")
            RetrofitClient.apiService.getCategory()
        }
    }


    //创建一个 安全调用方法 safeApiCall
    suspend fun <T> safeApiCall(apiCall: suspend () -> ResponseData<T>): ResponseData<T> {
        return try {
            val response = apiCall()
            Log.d("ApiCall", "返回的数据: $response")
            response
        } catch (e: Exception) {
            Log.e("ApiCall", "请求失败: ${e.message}")
            ResponseData(
                code = -1,
                msg = e.message ?: "未知错误",
                data = null
            )
        }
    }
}
