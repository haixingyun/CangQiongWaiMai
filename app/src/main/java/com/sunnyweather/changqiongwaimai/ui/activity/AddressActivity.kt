package com.sunnyweather.changqiongwaimai.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.sunnyweather.changqiongwaimai.R
import com.sunnyweather.changqiongwaimai.data.model.AddressRequest
import com.sunnyweather.changqiongwaimai.data.repository.AddressRepository
import com.sunnyweather.changqiongwaimai.databinding.ActivityAddressBinding
import com.sunnyweather.changqiongwaimai.ui.adapter.AddressAdapterss
import com.sunnyweather.changqiongwaimai.viewModel.AddressViewModel
import kotlinx.coroutines.launch

class AddressActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddressBinding
    private lateinit var addressRepository: AddressRepository
    private lateinit var addressAdapter: AddressAdapterss
    private val addressViewModel: AddressViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //设置activity状态栏颜色
        window.statusBarColor = ContextCompat.getColor(this, R.color.white)

        //获取控件
        val addressRecyclerView = binding.addressRecyclerView
        val btnAddAddress = binding.addAddressBook

        //初始化Repository
        addressRepository = AddressRepository()

        //初始化适配器
        setupRecyclerView()

        //订阅addressViewModel
        addressViewModel.address.observe(this) { result ->
            result?.let {
                addressAdapter.updateData(result)
            }
        }

        //事件：新增地址
        btnAddAddress.setOnClickListener {
            val intent = Intent(this, EditAddressActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupRecyclerView() {
        //为Recycle设置方向  默认是垂直方向
        binding.addressRecyclerView.layoutManager = LinearLayoutManager(this)
        //水平方向
        //addressRecyclerView.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)

        addressAdapter = AddressAdapterss(
            this, emptyList(), defaultAddress = { id ->
                lifecycleScope.launch {
                    //请求：根据id设置默认地址
                    addressRepository.settingDefaultAddress(AddressRequest(id))
                    addressViewModel.JianTingDiZhi() //发送请求
                }
            },
            editAddress = { id ->
                lifecycleScope.launch {
                    //请求：编辑收获地址
                    val intent =
                        Intent(this@AddressActivity, EditAddressActivity::class.java)
                    // 使用 putExtra 方法将 id 传递给 EditAddressActivity
                    intent.putExtra("address_id", id)
                    startActivity(intent)
                }
            }
        )

        binding.addressRecyclerView.adapter = addressAdapter
    }

    override fun onResume() {
        super.onResume()
        //发送请求
        addressViewModel.JianTingDiZhi()
    }

}