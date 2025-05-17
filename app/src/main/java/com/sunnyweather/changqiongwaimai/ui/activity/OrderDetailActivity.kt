package com.sunnyweather.changqiongwaimai.ui.activity
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hjq.toast.Toaster
import com.sunnyweather.changqiongwaimai.base.BaseActivity
import com.sunnyweather.changqiongwaimai.data.model.OrderResultData
import com.sunnyweather.changqiongwaimai.data.repository.CartRepository
import com.sunnyweather.changqiongwaimai.data.repository.OrderRepository
import com.sunnyweather.changqiongwaimai.databinding.ActivityOrderDetailBinding
import com.sunnyweather.changqiongwaimai.ui.adapter.OrderDetailAdapter
import com.sunnyweather.changqiongwaimai.viewModel.OrderViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OrderDetailActivity : BaseActivity() {
    private lateinit var binding: ActivityOrderDetailBinding
    private lateinit var OrderDetailRecycler: RecyclerView
    private lateinit var cartRepository: CartRepository
    private lateinit var orderRepository: OrderRepository
    private val orderDetailViewModel: OrderViewModel by viewModels()
    private var orderId: Int = 0
    //倒计时变量
    private lateinit var orderTimeString: String
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    private var countDownTimer: CountDownTimer? = null
    // 截止时间 = 下单时间 + 15 分钟
    private val paymentDurationMillis = 15 * 60 * 1000L


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityOrderDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //初始化网络类
        cartRepository = CartRepository()
        orderRepository = OrderRepository()
        //绑定recycler
        OrderDetailRecycler = binding.centerLayout.orderDetailRecyclerView
        OrderDetailRecycler.layoutManager = LinearLayoutManager(this)

        //获取前面activity传递的orderId
        orderId = intent.getIntExtra("order_id", -1)

        //发送请求
        orderDetailViewModel.getOrderDetail(orderId)

        //订阅根据id订单获取订单详情数据
        orderDetailViewModel.orderData.observe(this) { order ->
            order?.let {
                //为ui控件赋值
                when (order.status) {
                    1 -> {
                        binding.orderState.text = "等待支付"
                        binding.CuiDan.text = "立即支付"
                        binding.tips.visibility = View.VISIBLE
                        binding.DaoJiShi.visibility = View.VISIBLE
                         orderTimeString = order.orderTime // 下单时间字符串
                        //倒计时
                        startCountDown()
                        //事件 去支付
                        binding.CuiDan.setOnClickListener {
                            orderDetailViewModel._orderDetail.value = OrderResultData(
                                order.id,
                                order.amount,
                                order.number,
                                order.orderTime
                            )
                            val intent = Intent(this, PayActivity::class.java)
                            startActivity(intent)
                        }
                    }

                    2 -> {
                        binding.orderState.text = "等待商户接单"
                        //事件 催单
                        binding.CuiDan.setOnClickListener {
                            lifecycleScope.launch {
                                orderRepository.CuiDan(orderId)
                            }
                            // 显示长 Toast
                            Toaster.showLong("催单成功");
                        }
                    }

                    3 -> binding.orderState.text = "订单已接单"
                    4 -> binding.orderState.text = "订单派送中"
                    5 -> binding.orderState.text = "订单已完成"
                    6 -> {
                        binding.orderState.text = "订单已取消"
                        binding.cancelOrder.visibility = View.GONE
                        binding.CuiDan.visibility = View.GONE
                    }

                    7 -> binding.orderState.text = "订单已退款"
                }
                binding.centerLayout.DaBaoFei.text = "￥${order.packAmount}"
                binding.centerLayout.PeiSongFei.text = "￥6"
                binding.centerLayout.orderTotalPrice.text = "￥ ${order.amount}"
                binding.QiWangShiJian.text = order.estimatedDeliveryTime
                binding.PeiSongDiZhi.text = order.address
                binding.orderNumber.text = order.number
                binding.orderTime.text = order.orderTime

                when (order.payMethod) {
                    1 -> binding.payManner.text = "微信"
                    2 -> binding.payManner.text = "支付宝"
                }
                binding.CanJuShuLiang.text = order.tablewareNumber.toString()
                //绑定recycler的适配器
                OrderDetailRecycler.adapter = OrderDetailAdapter(this, order.orderDetailList)
            }
        }

        //事件 取消订单
        binding.cancelOrder.setOnClickListener {
            lifecycleScope.launch {
                orderRepository.cancelOrder(orderId)  //取消订单
                orderDetailViewModel.getOrderDetail(orderId)  //获取详细订单
            }
            binding.DaoJiShi.visibility = View.INVISIBLE  //让倒计时消失
            Toast.makeText(this, "取消成功", Toast.LENGTH_SHORT).show()
        }

        //事件 再来一单
        binding.zaiLaiYiDan.setOnClickListener {
            lifecycleScope.launch {
                cartRepository.emptyCart()   //清空购物车
                orderRepository.ZaiLaiYiDan(orderId)  //再来一单
                //跳转到MainActivity
                val intent = Intent(this@OrderDetailActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }

        }
        //事件 联系商家
        binding.LianXiShangJia.setOnClickListener {
            //AlertDialog弹出框
            val build = AlertDialog.Builder(this)
            build.setTitle("提示")
            build.setMessage("拨打？【仅为模拟】")
            build.setPositiveButton("确定") { dialog, which ->
                Toast.makeText(this, "点击了确定", Toast.LENGTH_SHORT).show()
            }
            build.setNegativeButton("取消") { dialog, which ->
                Toast.makeText(this, "点击了取消", Toast.LENGTH_SHORT).show()
            }
            build.show()
        }
    }

    private fun startCountDown() {
        val orderDate: Date? = dateFormat.parse(orderTimeString)
        if (orderDate == null) {
            binding.timeText.text = "时间格式错误"
            return
        }
        val deadline = orderDate.time + paymentDurationMillis
        val currentTime = System.currentTimeMillis()
        val timeRemaining = deadline - currentTime

        if (timeRemaining <= 0) {
            // 倒计时结束或订单已超时
            binding.timeText.text = "订单已超时"
            return
        }

        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(timeRemaining, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = (millisUntilFinished / (60 * 1000)).toInt()
                val seconds = ((millisUntilFinished / 1000) % 60).toInt()
                binding.timeText.text = String.format("%02d:%02d", minutes, seconds)
            }

            override fun onFinish() {
                binding.timeText.text = "订单已超时"
            }
        }.start()
    }


    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }

}