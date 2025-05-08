package com.sunnyweather.changqiongwaimai.data.repository

import android.util.Log
import com.sunnyweather.changqiongwaimai.data.api.RetrofitClient
import com.sunnyweather.changqiongwaimai.data.model.Order
import com.sunnyweather.changqiongwaimai.data.model.OrderEntity
import com.sunnyweather.changqiongwaimai.data.model.OrderPayment
import com.sunnyweather.changqiongwaimai.data.model.OrderResultData
import com.sunnyweather.changqiongwaimai.data.model.Orders
import com.sunnyweather.changqiongwaimai.data.model.ResponseData
import com.sunnyweather.changqiongwaimai.data.repository.OrderRepository.Companion.cachedOrder

class OrderRepository {

    companion object {
        var cachedOrder: OrderEntity = OrderEntity()
    }

    /**
     * 获取最近订单数据
     */
    suspend fun getOrders(): ResponseData<Orders> {
        return RetrofitClient.apiService.getOrders()
    }

    /**
     * 再来一单
     */
    suspend fun ZaiLaiYiDan(id: Int): ResponseData<*> {
        Log.d("ApiCall", "再来一单")
        return RetrofitClient.apiService.ZaiLaiYiDan(id)
    }

    /**
     * 根据订单id查看订单详情
     */
    suspend fun getOrderDetails(id: Int): ResponseData<Order> {
        Log.d("ApiCall", "根据订单id查看订单详情")
        return RetrofitClient.apiService.getOrderDetails(id)
    }

    /**
     * 查看全部订单数据
     */
    suspend fun getAllOrders(): ResponseData<Orders> {
        Log.d("ApiCall", "查看所有订单")
        return RetrofitClient.apiService.getAllOrders()
    }

    /**
     * 查看待付款订单
     */
    suspend fun getPendingOrders(): ResponseData<Orders> {
        Log.d("ApiCall", "查看待付款订单")
        return RetrofitClient.apiService.getAllOrders(status = "1")
    }

    /**
     * 查看已取消订单
     */
    suspend fun getCancelledOrders(): ResponseData<Orders> {
        Log.d("ApiCall", "查看已取消订单")
        return RetrofitClient.apiService.getAllOrders(status = "6")
    }

    /**
     * 提交订单
     */
    suspend fun submitOrderApi(): ResponseData<OrderResultData> {
        Log.d("ApiCall", "提交订单")
        return RetrofitClient.apiService.submitOrder(cachedOrder)
    }

    /**
     *  支付订单
     */
    suspend fun paymentOrder(orderPayment: OrderPayment): ResponseData<*> {
        Log.d("ApiCall", "支付订单")
        return RetrofitClient.apiService.paymentOrder(orderPayment)
    }

    /**
     * 催单
     */
    suspend fun CuiDan(orderId: Int): ResponseData<*> {
        Log.d("ApiCall", "催单")
        return RetrofitClient.apiService.CuiDan(orderId)
    }

    /**
     * 取消订单
     */
    suspend fun cancelOrder(orderId: Int): ResponseData<*> {
        Log.d("ApiCall", "取消订单")
        return RetrofitClient.apiService.cancelOrder(orderId)
    }

}