package com.sunnyweather.changqiongwaimai.data.model

data class OrderEntity(
    var addressBookId: Int? = 0,
    var amount: Int? = 0,
    var deliveryStatus: Int? = 0,
    var estimatedDeliveryTime: String? = "",
    var packAmount: Int? = 0,
    var payMethod: Int? = 0,
    var remark: String? = "",
    var tablewareNumber: Int? = 0,
    var tablewareStatus: Int? = 0
)

data class OrderResultData(
    val id: Int = 0,
    val orderAmount: Int = 0,
    val orderNumber: String = "",
    val orderTime: String = ""
)

data class OrderPayment(
    var orderNumber: String = "",
    var payMethod: Int = 0
)
