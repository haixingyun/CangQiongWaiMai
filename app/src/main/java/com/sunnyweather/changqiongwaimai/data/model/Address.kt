package com.sunnyweather.changqiongwaimai.data.model

data class Address(
    var cityCode: String = "",
    var cityName: String = "",
    var consignee: String = "",
    var detail: String = "",
    var districtCode: String = "",
    var districtName: String = "",
    var id: Int = -1,
    var isDefault: Int = 0,
    var label: String = "",
    var phone: String = "",
    var provinceCode: String = "",
    var provinceName: String = "",
    var sex: String = "",
    var userId: Int = -1
)


data class AddressRequest(val id: Int)

