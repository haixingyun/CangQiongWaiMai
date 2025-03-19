package com.sunnyweather.changqiongwaimai.data.model

data class Goods(
    val id: Int,
    val name: String,
    val categoryId: Int,
    val price: Double,
    val image: String,
    val description: String,
    val number: Int,
    val status: Int,
    val amount: Int,
    val updateTime: String,
    val categoryName: String?,
    val flavors: List<Flavor>,
    val quantity: Int = 0,
)