package com.sunnyweather.changqiongwaimai.data.model

data class Province(val name: String, val city: List<City>)
data class City(val name: String, val area: List<String>)
