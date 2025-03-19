package com.sunnyweather.changqiongwaimai.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sunnyweather.changqiongwaimai.R
import com.sunnyweather.changqiongwaimai.data.model.AddressRequest
import com.sunnyweather.changqiongwaimai.data.repository.AddressRepository
import com.sunnyweather.changqiongwaimai.databinding.ActivityAddressBinding
import com.sunnyweather.changqiongwaimai.ui.adapter.AddressAdapter
import com.sunnyweather.changqiongwaimai.viewModel.AddressViewModel
import kotlinx.coroutines.launch

class AddressActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddressBinding
    private lateinit var addressRepository: AddressRepository
    private val addressViewModel: AddressViewModel by viewModels()
    private lateinit var addressRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = ContextCompat.getColor(this,R.color.white)

        addressRepository = AddressRepository()

        addressRecyclerView = binding.addressRecyclerView
        addressRecyclerView.layoutManager = LinearLayoutManager(this)


        //订阅addressViewModel
        addressViewModel.address.observe(this) { result ->
            result?.let {
                addressRecyclerView.adapter = AddressAdapter(
                    this, result, defaultAddress = { id ->
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
            }
        }

        binding.addAddressBook.setOnClickListener {
            val intent = Intent(this, EditAddressActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        //发送请求
        addressViewModel.JianTingDiZhi()
    }

}