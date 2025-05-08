package com.sunnyweather.changqiongwaimai.viewModel

import androidx.lifecycle.MutableLiveData
import com.sunnyweather.changqiongwaimai.base.BaseViewModel
import com.sunnyweather.changqiongwaimai.data.model.Order
import com.sunnyweather.changqiongwaimai.data.model.OrderResultData
import com.sunnyweather.changqiongwaimai.data.model.Orders
import com.sunnyweather.changqiongwaimai.data.repository.OrderRepository

/**
 * 订单viewModel
 */
class OrderViewModel : BaseViewModel() {

    val orderId = MutableLiveData<Int>()
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
        when (orderStatus) {
            "全部订单" -> {
                request(
                    request = { repository.getAllOrders() },
                    onSuccess = { data -> _posts.value = data }
                )
            }

            "待付款" -> {
                request(
                    request = { repository.getPendingOrders() },
                    onSuccess = { data -> _posts.value = data }
                )
            }

            "已取消" -> {
                request(
                    request = { repository.getCancelledOrders() },
                    onSuccess = { data -> _posts.value = data }
                )

            }

            else -> {
                request(
                    request = { repository.getOrders() },
                    onSuccess = { data -> _posts.value = data }
                )
            }
        }
    }

    /**
     * 根据id获取订单数据
     */
    fun getOrderDetail(order: Int) {
        request(
            request = { repository.getOrderDetails(order) },
            onSuccess = { data -> _orderData.value = data }
        )
    }

    /**
     * 提交订单
     */
    fun submitOrder() {
        request(
            request = { repository.submitOrderApi() },
            onSuccess = { data -> _orderDetail.value = data }
        )
    }
}