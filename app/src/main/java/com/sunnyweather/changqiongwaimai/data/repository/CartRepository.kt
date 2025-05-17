package com.sunnyweather.changqiongwaimai.data.repository

import android.util.Log
import com.sunnyweather.changqiongwaimai.data.api.RetrofitClient
import com.sunnyweather.changqiongwaimai.data.model.Dish
import com.sunnyweather.changqiongwaimai.data.model.Flavor
import com.sunnyweather.changqiongwaimai.data.model.ResponseData

class CartRepository {

    /**
     * 将商品添加到购物车
     */
    suspend fun addToCart(flavor: Flavor): ResponseData<*> {
        val response = RetrofitClient.apiService.addCartGood(flavor)
        return response
    }

    /**
     * 购物车减少商品
     */
    suspend fun cartSubtractGoods(flavor: Flavor): ResponseData<*> {
        return RetrofitClient.apiService.subtractCartGood(flavor)
    }

    /**
     * 获取购物车的商品
     */
    suspend fun getCart(): ResponseData<List<Dish>> {
        Log.d("ApiCall", "购物车数据")
        return RetrofitClient.apiService.getCartGood()
    }

    /**
     * 清空购物车
     */
    suspend fun emptyCart(): ResponseData<*> {
        Log.d("ApiCall", "清空购物车")
        return RetrofitClient.apiService.emptyCartGoods()
    }
}
