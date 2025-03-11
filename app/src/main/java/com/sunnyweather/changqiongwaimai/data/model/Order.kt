package com.sunnyweather.changqiongwaimai.data.model

data class Orders (
    val total: Int,
    val records : List<Order>?
)

data class Order(
    val address: String,
    val addressBookId: Int,
    val amount: Int,
    val cancelReason: String?,
    val cancelTime: String?,
    val checkoutTime: String?,
    val consignee: String,
    val deliveryStatus: Int,
    val deliveryTime: String?,
    val estimatedDeliveryTime: String,
    val id: Int,
    val number: String,
    val orderDetailList: List<OrderDetail>,
    val orderDishes: Any?, // 这里是 null，根据你的数据，可以适配为 Any 类型，或者更具体的类型
    val orderTime: String,
    val packAmount: Int,
    val payMethod: Int,
    val payStatus: Int,
    val phone: String,
    val rejectionReason: String?,
    val remark: String?,
    val status: Int,
    val tablewareNumber: Int,
    val tablewareStatus: Int,
    val userId: Int,
    val userName: String?
)


data class OrderDetail(
    val amount: Double = 0.0,
    val dishFlavor: String? = "",
    val dishId: Int? = null,
    val id: Int? = null,
    val image: String? = null,
    val name: String? = null,
    val number: Int? = null,
    val orderId: Int = 0,
    val setmealId: Int? = null
)


