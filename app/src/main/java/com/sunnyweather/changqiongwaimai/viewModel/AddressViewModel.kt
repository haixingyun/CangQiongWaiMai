package com.sunnyweather.changqiongwaimai.viewModel

import androidx.lifecycle.MutableLiveData
import com.sunnyweather.changqiongwaimai.base.BaseViewModel
import com.sunnyweather.changqiongwaimai.data.model.Address
import com.sunnyweather.changqiongwaimai.data.repository.AddressRepository

/**
 * 地址viewmodel
 */
class AddressViewModel : BaseViewModel() {
    private val addressRepository = AddressRepository()

    // 用户地址列表
    private val _address = MutableLiveData<List<Address>?>()
    val address: MutableLiveData<List<Address>?> = _address

    private val _editAddress = MutableLiveData<Address?>()
    val editAddress: MutableLiveData<Address?> = _editAddress

    private val _defaultAddress = MutableLiveData<Address?>()
    val defaultAddress: MutableLiveData<Address?> = _defaultAddress

    /**
     * 获取地址数据
     */
    fun JianTingDiZhi() {
        request(
            request = { addressRepository.getAllAddress() },
            onSuccess = { data -> _address.postValue(data) }
        )
    }

    /**
     * 监听根据id获取地址数据
     */
    fun editAddress(id: Int) {
        request(
            request = { addressRepository.editAddress(id) },
            onSuccess = { data -> _editAddress.postValue(data) }
        )
    }

    /**
     * 获取默认地址
     */
    fun getDefaultAddress() {
        request(
            request = { addressRepository.getDefaultAddress() },
            onSuccess = { data -> _defaultAddress.postValue(data) }
        )
    }
}