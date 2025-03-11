package com.sunnyweather.changqiongwaimai.data.model

data class Flavors(
    val id: Int, //口味id
    val dishId: Int,    // 菜品id
    val name: String, // 口味标题
    val value: String?,    // 口味详细
)

data class Flavor(
    val dishId: Int,    // 菜品id
    val dishFlavor: String? = null
)