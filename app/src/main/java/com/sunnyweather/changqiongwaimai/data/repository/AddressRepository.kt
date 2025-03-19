package com.sunnyweather.changqiongwaimai.data.repository

import android.util.Log
import com.sunnyweather.changqiongwaimai.data.api.RetrofitClient
import com.sunnyweather.changqiongwaimai.data.model.Address
import com.sunnyweather.changqiongwaimai.data.model.AddressRequest
import com.sunnyweather.changqiongwaimai.data.model.ResponseData
import com.sunnyweather.changqiongwaimai.utils.ApiHelper

class AddressRepository {

    /**
     * 根据id设置为默认地址
     */
    suspend fun settingDefaultAddress(addressRequest: AddressRequest): ResponseData<*> {
        return ApiHelper.safeApiCall {
            Log.d("AddressApi", "设置id:$addressRequest 为默认地址")
            RetrofitClient.apiService.settingDefaultAddress(addressRequest)
        }
    }

    /**
     * 查询所有收获地址
     */
    suspend fun getAllAddress(): ResponseData<List<Address>> {
        return ApiHelper.safeApiCall {
            Log.d("AddressApi", "查询所有收获地址")
            RetrofitClient.apiService.getAddressList()
        }
    }

    /**
     * 根据id获取地址
     */
    suspend fun editAddress(id: Int): ResponseData<Address> {
        return ApiHelper.safeApiCall {
            Log.d("AddressApi", "获取id:$id 收获地址")
            RetrofitClient.apiService.editAddressBook(id, id)
        }
    }

    /**
     * 修改收获地址
     */
    suspend fun putAddress(address: Address): ResponseData<*> {
        return ApiHelper.safeApiCall {
            Log.d("AddressApi", "修改id${address.id}收获地址")
            RetrofitClient.apiService.editAddressBook(address)
        }
    }


    /**
     * 根据id删除地址
     */
    suspend fun deleteAddressBook(id: Int): ResponseData<*> {
        return ApiHelper.safeApiCall {
            RetrofitClient.apiService.deleteAddressBook(id)
        }
    }

    /**
     * 修改收获地址
     */
    suspend fun saveAddressBook(address: Address): ResponseData<*> {
        return ApiHelper.safeApiCall {
            Log.d("AddressApi", "新增id${address.id}的地址")
            RetrofitClient.apiService.saveAddressBook(address)
        }
    }

    /**
     * 查询默认的收获地址
     */
    suspend fun getDefaultAddress() : ResponseData<Address?> {
        return ApiHelper.safeApiCall {
            RetrofitClient.apiService.getDefaultAddress()
        }
    }

}