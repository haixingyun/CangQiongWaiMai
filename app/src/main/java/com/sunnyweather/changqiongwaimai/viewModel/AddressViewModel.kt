package com.sunnyweather.changqiongwaimai.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sunnyweather.changqiongwaimai.data.model.Address
import com.sunnyweather.changqiongwaimai.data.repository.AddressRepository
import kotlinx.coroutines.launch

class AddressViewModel : ViewModel() {
    private val addressRepository = AddressRepository()


    private val _address = MutableLiveData<List<Address>?>()
    val address: MutableLiveData<List<Address>?> = _address

    private val _editAddress = MutableLiveData<Address?>()
    val editAddress: MutableLiveData<Address?> = _editAddress

    private val _defaultAddress = MutableLiveData<Address?>()
    val defaultAddress: MutableLiveData<Address?> = _defaultAddress

    /**
     * 监听地址数据
     */
    fun JianTingDiZhi() {
        viewModelScope.launch {
            val result = addressRepository.getAllAddress()
            if (result.code == 1) _address.value = result.data
        }
    }

    /**
     * 监听根据id获取地址数据
     */
    fun editAddress(id: Int) {
        viewModelScope.launch {
            val result = addressRepository.editAddress(id)
            if (result.code == 1) editAddress.value = result.data
        }
    }

    /**
     * 默认地址数据
     */

    fun getDefaultAddress() {
        viewModelScope.launch {
            val result = addressRepository.getDefaultAddress()
            if (result.code == 1) defaultAddress.value = result.data
        }
    }

}