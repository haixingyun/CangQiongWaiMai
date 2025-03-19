package com.sunnyweather.changqiongwaimai.ui.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.sunnyweather.changqiongwaimai.R
import com.sunnyweather.changqiongwaimai.data.model.Address
import com.sunnyweather.changqiongwaimai.data.repository.AddressRepository
import com.sunnyweather.changqiongwaimai.databinding.ActivityEditAddressBinding
import com.sunnyweather.changqiongwaimai.viewModel.AddressViewModel
import kotlinx.coroutines.launch

class EditAddressActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditAddressBinding
    private val addressViewModel: AddressViewModel by viewModels()
    private lateinit var addressRepository: AddressRepository
    private lateinit var Address1: Address

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityEditAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)


        addressRepository = AddressRepository()  //初始化地址网路
        Address1 = Address()
        var index: Int = 0

        val addressId = intent.getIntExtra("address_id", -1)

        if (addressId == -1) {
            binding.toolbarTitle.text = "新增地址"
            binding.deleteAddress.visibility = View.GONE
        } else {
            //发送根据di获取地址数据viewModel请求 ， 触发订阅
            addressViewModel.editAddress(addressId)
        }

        //订阅根据id获取数据
        addressViewModel.editAddress.observe(this) { address ->
            address?.let {
                Address1 = address.copy()
                binding.name.setText(address.consignee)
                binding.phone.setText(address.phone)
                binding.address.text = this.getString(
                    R.string.address_format,
                    address.provinceName, address.cityName, address.districtName
                )
                binding.XiangXiDiZhi.setText(address.detail)
                when (address.label) {
                    "1" -> binding.labelGongShi.setBackgroundColor(Color.parseColor("#FFCC99"))
                    "2" -> binding.labelJia.setBackgroundColor(Color.parseColor("#CCFFFF"))
                    "3" -> binding.labelSchool.setBackgroundColor(Color.parseColor("#CCFFCC"))
                }
                when (address.sex) {
                    "0" -> {
                        binding.radioMen.setBackgroundResource(R.drawable.gender_background_xuanzhong)
                        binding.radioMen.isChecked = true
                    }
                    "1" -> {
                        binding.radioMen.setBackgroundResource(R.drawable.gender_background)
                        binding.radioGirl.setBackgroundResource(R.drawable.gender_background_xuanzhong)
                        binding.radioGirl.isChecked = true
                    }
                }
            }
        }

        binding.nameEmpty.setOnClickListener {
            binding.name.setText("")
        }
        binding.phoneEmpty.setOnClickListener {
            binding.phone.setText("")
        }
        //点击男士radio
        binding.radioMen.setOnClickListener {
            binding.radioMen.setBackgroundResource(R.drawable.gender_background_xuanzhong)
            binding.radioGirl.setBackgroundResource(R.drawable.gender_background)
            binding.radioMen.isChecked = true
            binding.radioGirl.isChecked = false
        }
        binding.radioGirl.setOnClickListener {
            binding.radioMen.setBackgroundResource(R.drawable.gender_background)
            binding.radioGirl.setBackgroundResource(R.drawable.gender_background_xuanzhong)
            binding.radioMen.isChecked = false
            binding.radioGirl.isChecked = true
        }

        binding.labelJia.setOnClickListener {
            binding.labelJia.setBackgroundColor(Color.parseColor("#CCFFFF"))
            binding.labelGongShi.setBackgroundResource(R.drawable.rectangle_border)
            binding.labelSchool.setBackgroundResource(R.drawable.rectangle_border)
            index = 2
        }
        binding.labelGongShi.setOnClickListener {
            binding.labelGongShi.setBackgroundColor(Color.parseColor("#FFCC99"))
            binding.labelJia.setBackgroundResource(R.drawable.rectangle_border)
            binding.labelSchool.setBackgroundResource(R.drawable.rectangle_border)
            index = 1
        }
        binding.labelSchool.setOnClickListener {
            binding.labelSchool.setBackgroundColor(Color.parseColor("#CCFFCC"))
            binding.labelGongShi.setBackgroundResource(R.drawable.rectangle_border)
            binding.labelJia.setBackgroundResource(R.drawable.rectangle_border)
            index = 3
        }

        //监听name edit组件数据是否为空
        binding.name.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty()) {
                    binding.nameEmpty.visibility = View.GONE  // 隐藏 ImageView
                } else {
                    binding.nameEmpty.visibility = View.VISIBLE  // 显示 ImageView
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        //监听edit组件
        binding.phone.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty()) {
                    binding.phoneEmpty.visibility = View.GONE  // 隐藏 ImageView
                } else {
                    binding.phoneEmpty.visibility = View.VISIBLE  // 显示 ImageView
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })


        binding.DiZhi.setOnClickListener {

        }

        //事件：点击保存地址
        binding.saveAddress.setOnClickListener {
            //校验名字是否为空
            if (binding.name.text.isNullOrEmpty()) {
                Toast.makeText(this, "名字不能为空", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 校验手机号是否为11位有效手机号
            if (binding.phone.text.isNullOrEmpty()) {
                Toast.makeText(this, "手机号不能为空", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val phone = binding.phone.text.toString()
            val phoneRegex = "^1[3-9]\\d{9}$"  // 正则表达式，匹配中国大陆手机号
            if (!phone.matches(phoneRegex.toRegex())) {
                Toast.makeText(this, "请输入有效的11位手机号", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            Address1.consignee = binding.name.text.toString()
            Address1.phone = binding.phone.text.toString()
            Address1.sex = if (binding.radioMen.isChecked) "0" else "1"

            val addressText = binding.address.text.toString()
            Address1.provinceName =
                if (addressText.length >= 3) addressText.substring(0, 3) else addressText
            Address1.cityName = if (addressText.length >= 6) addressText.substring(3, 6) else ""
            Address1.districtName = addressText
            if (index != 0) Address1.label = index.toString()
            //在协程中调用
            lifecycleScope.launch {
                if (addressId == -1) addressRepository.saveAddressBook(Address1)
                else addressRepository.putAddress(Address1)
                //跳转到AddressActivity
                val intent = Intent(this@EditAddressActivity, AddressActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        //事件：点击删除地址
        binding.deleteAddress.setOnClickListener {
            lifecycleScope.launch {
                val result = addressRepository.deleteAddressBook(addressId)
                if (result.code == 1) {
                    // 删除完成后，跳转并显示提示
                    val intent = Intent(this@EditAddressActivity, AddressActivity::class.java)
                    startActivity(intent)
                    finish()
                    Toast.makeText(this@EditAddressActivity, "删除成功", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(
                        this@EditAddressActivity,
                        "删除失败:${result.msg}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}