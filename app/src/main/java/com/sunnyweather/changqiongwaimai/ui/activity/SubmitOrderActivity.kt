package com.sunnyweather.changqiongwaimai.ui.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sunnyweather.changqiongwaimai.base.BaseActivity
import com.sunnyweather.changqiongwaimai.data.model.OrderDetail
import com.sunnyweather.changqiongwaimai.data.model.OrderEntity
import com.sunnyweather.changqiongwaimai.data.repository.OrderRepository
import com.sunnyweather.changqiongwaimai.databinding.OrderSubmitActivityBinding
import com.sunnyweather.changqiongwaimai.ui.adapter.OrderDetailAdapter
import com.sunnyweather.changqiongwaimai.viewModel.AddressViewModel
import com.sunnyweather.changqiongwaimai.viewModel.CartViewModel
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * 提交订单时的Activity
 */
class SubmitOrderActivity : BaseActivity() {
    private lateinit var binding: OrderSubmitActivityBinding
    private val cartViewModel: CartViewModel by viewModels()
    private val addressViewModel: AddressViewModel by viewModels()
    private lateinit var OrderDetailRecycler: RecyclerView
    private lateinit var orderEntity: OrderEntity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = OrderSubmitActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.getWindowInsetsController(window.decorView)?.apply {
            isAppearanceLightStatusBars = true // 文字黑色
        }
        //Recycler初始化
        OrderDetailRecycler = binding.JieSuan.orderDetailRecyclerView
        OrderDetailRecycler.layoutManager = LinearLayoutManager(this)

        //构建提交对象，发送请求的参数
        orderEntity = OrderEntity()

        // 监听购物车数据变化
        cartViewModel.posts.observe(this) { cartData ->

            val orderDetailList = cartData?.map { dish ->
                OrderDetail(
                    name = dish.name,
                    image = dish.image,
                    number = dish.number,
                    amount = dish.amount.toDouble(),
                    dishFlavor = dish.dishFlavor
                )
            } ?: emptyList()

            //为控件赋值
            val totalPrice = cartData?.sumOf { it.number * it.amount } ?: 0//购物车总价
            val DaBaoFei = cartData?.sumOf { it.number ?: 0 } ?: 0  //打包费
            val PeiSongFei = DaBaoFei * 2   //配送费

            val totalAmount = totalPrice + DaBaoFei + PeiSongFei
            val formattedPrice = String.format("%.2f", totalAmount?.toDouble())  //格式化总价为两位小数

            binding.JieSuan.orderTotalPrice.text = "￥$formattedPrice"  //商品详细总价
            binding.JieSuan.DaBaoFei.text = "￥$DaBaoFei"  //打包费
            binding.JieSuan.PeiSongFei.text = "￥$PeiSongFei"  //配送费
            binding.floatingCart.cartBadge.text = cartData?.sumOf { it.number }.toString()  //总数量
            binding.floatingCart.cartTotalPrice.text = "￥${totalAmount}"  //购物车总价


            //为viewModel的orderEntity赋值，这是请求参数
            orderEntity.amount = totalAmount
            orderEntity.packAmount = cartData?.sumOf { it.number }  //赋值打包费


            // 处理购物车数据（例如显示在页面上）
            OrderDetailRecycler.adapter = OrderDetailAdapter(this, orderDetailList)
        }

        //监听默认地址数据
        addressViewModel.defaultAddress.observe(this) { addressDate ->
            addressDate?.let {

                binding.ShouHuoRen.text = it.consignee  //收货人
                binding.addressPhone.text = it.phone //手机号

                orderEntity.addressBookId = addressDate.id
                when (it.label) {
                    "1" -> {
                        binding.addressLable.setBackgroundColor(Color.parseColor("#FFCC99"))
                        binding.addressLable.text = "公司"
                    }

                    "2" -> {
                        binding.addressLable.setBackgroundColor(Color.parseColor("#CCFFFF"))
                        binding.addressLable.text = "家"
                    }

                    "3" -> {
                        binding.addressLable.setBackgroundColor(Color.parseColor("#CCFFCC"))
                        binding.addressLable.text = "学校"
                    }
                }
                binding.DiZhiBiaoTi.visibility = View.GONE
                binding.address.visibility = View.VISIBLE
                binding.addressName.text =
                    "${it.provinceName}${it.cityName}${it.districtName}${it.detail}"

                val now = LocalTime.now().plusMinutes(20) // 获取当前时间并加 20 分钟
                val formatter = DateTimeFormatter.ofPattern("HH:mm") // 格式化为 时:分
                binding.SongDaShiJian.text = now.format(formatter) // 设置文本
            }
            if (addressDate == null) {
                binding.DiZhiBiaoTi.visibility = View.VISIBLE
                binding.ShouHuoBiaoTi.visibility = View.GONE
                binding.tvPeiSongPrompt.visibility = View.GONE
                binding.address.visibility = View.GONE
                binding.tvPeiSongPrompt2.visibility = View.GONE
                binding.llSngDaTime.visibility = View.GONE
            }
        }

        //预计送达时间往后推一个小时
        val estimatedTime = LocalDateTime.now().plusHours(1)
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val formattedTime = estimatedTime.format(formatter)
        orderEntity.estimatedDeliveryTime = formattedTime
        orderEntity.payMethod = 1  //支付方式
        orderEntity.remark = ""  //备注
        orderEntity.tablewareNumber = binding.CanJuShuLiang.text?.toString()?.toIntOrNull() ?: 0
        orderEntity.deliveryStatus = 0  //餐具状态

        //事件 地址
        binding.address.setOnClickListener {
            val intent = Intent(this, AddressActivity::class.java)
            startActivity(intent)
        }
        binding.PeiSong.setOnClickListener {
            showInputDialog()
        }
        binding.floatingCart.cartCheckout.setOnClickListener {
            OrderRepository.cachedOrder = orderEntity
            val intent = Intent(this, PayActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.DiZhiBiaoTi.setOnClickListener {
            val intent = Intent(this, AddressActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onResume() {
        super.onResume()
        // 调用获取数据接口，确保获取最新数据
        cartViewModel.getCart()
        //调用默认地址接口
        addressViewModel.getDefaultAddress()
    }


    private fun showInputDialog() {
        val editText = EditText(this).apply {
            hint = "请输入内容"
        }

        // 用 LinearLayout 包裹 EditText，并设置 padding
        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 20, 50, 0) // 左右 50dp, 上 20dp, 下 0dp（根据需要调）
            addView(editText)
        }

        val dialog = AlertDialog.Builder(this)
            .setTitle("提示")
            .setView(container)
            .setPositiveButton("确定") { _, _ ->
                val inputText = editText.text.toString()
                orderEntity.remark = inputText
                binding.content.text = inputText
            }
            .setNegativeButton("取消", null)
            .create()

        dialog.show()
    }
}