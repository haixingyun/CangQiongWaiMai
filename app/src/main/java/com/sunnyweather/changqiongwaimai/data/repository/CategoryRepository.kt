package com.sunnyweather.changqiongwaimai.data.repository

import com.sunnyweather.changqiongwaimai.data.api.RetrofitClient
import com.sunnyweather.changqiongwaimai.data.model.AddressRequest
import com.sunnyweather.changqiongwaimai.data.model.Goods
import com.sunnyweather.changqiongwaimai.data.model.ResponseData
import com.sunnyweather.changqiongwaimai.utils.ApiHelper

class CategoryRepository {

    /**
     * 根据分类id查询菜品
     */
    suspend fun categoryIdGetDish(categoryId:Int) : ResponseData<List<Goods>> {
        return ApiHelper.safeApiCall {
            RetrofitClient.apiService.getGoodsData(categoryId)
        }
    }

}