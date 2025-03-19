package com.sunnyweather.changqiongwaimai.data.model

data class Dish(
    val amount: Int,
    val createTime: String,
    val dishFlavor: String?,  // 可为 null
    val dishId: Int,
    val id: Int,
    val image: String,
    val name: String,
    val number: Int,
    val setmealId: Int?,      // 可为 null
    val userId: Int
)
