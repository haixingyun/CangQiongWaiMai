package com.sunnyweather.changqiongwaimai.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sunnyweather.changqiongwaimai.data.model.Order
import com.sunnyweather.changqiongwaimai.data.model.OrderEntity
import com.sunnyweather.changqiongwaimai.data.model.OrderResultData
import com.sunnyweather.changqiongwaimai.data.model.Orders
import com.sunnyweather.changqiongwaimai.data.repository.OrderRepository
import kotlinx.coroutines.launch

class OrderViewModel : ViewModel() {

    val orderId = MutableLiveData<Int>()

    val orderEntity = MutableLiveData<OrderEntity>()

    //存储payment订单信息
    val paymentOrder = MutableLiveData<OrderResultData>()

    private val repository = OrderRepository()

    private val _posts = MutableLiveData<Orders?>()
    val post: MutableLiveData<Orders?> = _posts


    private val _orderData = MutableLiveData<Order?>()
    val orderData: MutableLiveData<Order?> = _orderData

    val _orderDetail = MutableLiveData<OrderResultData?>()
    val orderDetail: MutableLiveData<OrderResultData?> = _orderDetail

    /**
     * 查询订单数据
     */
    fun getOrders(orderStatus: String) {
        viewModelScope.launch {
            when (orderStatus) {
                "全部订单" -> {
                    // 请求全部订单数据
                    val result = repository.getAllOrders()
                    if (result.code == 1) _posts.value = result.data
                }

                "待付款" -> {
                    // 请求待付款订单数据
                    val result = repository.getPendingOrders()
                    if (result.code == 1) _posts.value = result.data
                }

                "已取消" -> {
                    // 请求已取消订单数据
                    val result = repository.getCancelledOrders()
                    if (result.code == 1) _posts.value = result.data
                }

                else -> {
                    // 请求全最近订单数据
                    val result = repository.getOrders()
                    if (result.code == 1) _posts.value = result.data
                }
            }
        }
    }

    /**
     * 根据id获取订单数据
     */
    fun getOrderDetail(order: Int) {
        viewModelScope.launch {
            val result = repository.getOrderDetails(order)
            if (result.code == 1) _orderData.value = result.data
        }
    }

    /**
     * 提交订单
     */
    fun submitOrder() {
        viewModelScope.launch {
            val result = repository.submitOrderApi()
            if (result.code == 1) _orderDetail.value = result.data
        }
    }

}