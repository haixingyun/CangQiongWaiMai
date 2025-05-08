package com.sunnyweather.changqiongwaimai.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sunnyweather.changqiongwaimai.data.model.ResponseData
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

/**
 * viewModel基类
 */
open class BaseViewModel : ViewModel() {

    // 全局错误信息
    protected val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    /**
     * 通用请求封装：自动捕获网络异常 & 统一处理 code
     * @param request   suspend lambda 发起接口请求，返回 ResponseData<T>
     * @param onSuccess lambda 请求成功(code==1)时回调，data可能为null
     */
    protected fun <T> request(
        request: suspend () -> ResponseData<T>,
        onSuccess: (T?) -> Unit = {}
    ) {
        // 协程异常处理器：捕获网络或解析异常
        val handler = CoroutineExceptionHandler { _, exception ->
            _error.postValue(exception.message ?: "网络异常，请稍后重试")
        }

        viewModelScope.launch(handler) {
            val result = request()
            if (result.code == 1) {
                onSuccess(result.data)
            } else {
                // code==0 或其他状态都走这里
                _error.postValue(result.msg ?: "操作失败")
            }
        }
    }
}