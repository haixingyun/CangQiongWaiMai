package com.sunnyweather.changqiongwaimai.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.sunnyweather.changqiongwaimai.R
import com.sunnyweather.changqiongwaimai.base.BaseActivity
import com.sunnyweather.changqiongwaimai.databinding.ActivityMyBinding
import com.sunnyweather.changqiongwaimai.ui.fragment.RecyclerFragment
import com.sunnyweather.changqiongwaimai.viewModel.OrderViewModel

/**
 * 个人中心activity
 */
class MyActivity : BaseActivity() {
    private lateinit var binding: ActivityMyBinding
    private val orderViewModel: OrderViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.enableEdgeToEdge()
        binding = ActivityMyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //状态栏设置为黄色
        window.statusBarColor = ContextCompat.getColor(this, R.color.yellow)

        // //只在 Activity 首次创建 时执行 （即 savedInstanceState == null 时）
        if (savedInstanceState == null) {
            // 传递参数时确认
            val fragment = RecyclerFragment.newInstance("最近订单")
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer,fragment)
                .commit()
        }

        orderViewModel.post.observe(this) { order ->
            order?.let {
                if (it.total == 0) {
                    binding.ZuiJinDingDan.visibility = View.GONE  // 隐藏 View
                    binding.MeiYouGengDuo.text = "还没有订单~~~"
                } else {
                    binding.ZuiJinDingDan.visibility = View.VISIBLE  // 重新显示 View
                    binding.MeiYouGengDuo.text = "没有更多了~~~"
                }
            }
        }


        binding.llAddressGo.setOnClickListener {
            val intent = Intent(this, AddressActivity::class.java)
            startActivity(intent)
        }

        binding.LLTwo.setOnClickListener {
            val intent = Intent(this, HistoryOrderActivity::class.java)
            startActivity(intent)
        }

        binding.imFanHui.setOnClickListener {
            finish()
        }
    }

}