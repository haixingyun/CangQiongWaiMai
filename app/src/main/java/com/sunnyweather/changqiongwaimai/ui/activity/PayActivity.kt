package com.sunnyweather.changqiongwaimai.ui.activity

import android.os.Bundle
import android.os.CountDownTimer
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.lifecycle.lifecycleScope
import com.sunnyweather.changqiongwaimai.R
import com.sunnyweather.changqiongwaimai.base.BaseActivity
import com.sunnyweather.changqiongwaimai.data.model.OrderPayment
import com.sunnyweather.changqiongwaimai.data.repository.OrderRepository
import com.sunnyweather.changqiongwaimai.databinding.OrderPayActivityBinding
import com.sunnyweather.changqiongwaimai.ui.fragment.OrderSuccessFragment
import com.sunnyweather.changqiongwaimai.viewModel.OrderViewModel
import kotlinx.coroutines.launch


/**
 * 用户支付Activity
 */
class PayActivity : BaseActivity() {

    private lateinit var binding: OrderPayActivityBinding
    private val orderViewModel: OrderViewModel by viewModels()
    private lateinit var orderRepository: OrderRepository

    //支付请求参数
    private lateinit var paymentOrder: OrderPayment

    // 将计时器声明在类级别，供 onDestroy() 中访问
    private var countDownTimer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        paymentOrder = OrderPayment()
        binding = OrderPayActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //设置状态栏颜色
        ViewCompat.getWindowInsetsController(window.decorView)?.apply {
            isAppearanceLightStatusBars = true // 文字黑色
        }

        orderRepository = OrderRepository()
        //监听提交请求返回数据
        orderViewModel.orderDetail.observe(this) { orderDetail ->
            orderDetail?.let {
                //将orderId存储起来供其他fragment访问
                binding.orderCode.text = "苍穹餐厅-${orderDetail.orderNumber}"
                binding.price.text = orderDetail.orderAmount.toString()
                paymentOrder.orderNumber = orderDetail.orderNumber
                orderViewModel.orderId.value = orderDetail.id
            }
        }

        //如果微信支付是选中状态，为参数赋值1
        if (binding.DanXuanKuang.isChecked) {
            paymentOrder.payMethod = 1
        } else {
            paymentOrder.payMethod = 2
        }

        //开启倒计时
        startCountDown()

        //事件：确认支付
        binding.QueREnZhiFu.setOnClickListener {
            //请求 订单支付
            lifecycleScope.launch {
                orderRepository.paymentOrder(paymentOrder)
            }
            // 添加 Fragment 到容器
            supportFragmentManager.beginTransaction()
                .replace(
                    R.id.fragment_container,
                    OrderSuccessFragment()
                ) // R.id.fragment_container 是 Activity 中的 FrameLayout
                .commit()
        }
    }

    /**
     * 开启倒计时
     */
    private fun startCountDown() {
        // 10 分钟 = 10 * 60 * 1000 毫秒
        val totalTime = 10 * 60 * 1000L

        // 创建并赋值给类级别的 countDownTimer
        countDownTimer = object : CountDownTimer(totalTime, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val totalSeconds = millisUntilFinished / 1000
                val minutes = totalSeconds / 60
                val seconds = totalSeconds % 60
                binding.time.text = String.format(
                    "支付剩余时间 %02d:%02d", minutes, seconds
                )
            }

            override fun onFinish() {
                binding.time.text = "订单已超时"
            }
        }

        // 启动计时器
        countDownTimer?.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        // 在 Fragment 销毁时取消倒计时，防止内存泄漏或重复计时
        countDownTimer?.cancel()
        countDownTimer = null
    }

    override fun onResume() {
        super.onResume()
        orderViewModel.submitOrder()
    }
}
