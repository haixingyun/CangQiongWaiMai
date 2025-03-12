package com.sunnyweather.changqiongwaimai.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.sunnyweather.changqiongwaimai.R
import com.sunnyweather.changqiongwaimai.databinding.ActivityMyBinding
import com.sunnyweather.changqiongwaimai.ui.fragment.RecyclerFragment
import com.sunnyweather.changqiongwaimai.viewModel.OrderViewModel

class MyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyBinding
    private val orderViewModel: OrderViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.enableEdgeToEdge()
        binding = ActivityMyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = ContextCompat.getColor(this, R.color.yellow)

        // 在 MyActivity 中加载 RecyclerFragment
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(
                    R.id.fragmentContainer,
                    RecyclerFragment.newInstance("")
                )  // 使用 FragmentContainerView 来加载 RecyclerFragment
                .commit()
        }

        orderViewModel.post.observe(this) { order ->
            order?.let {
                if (it.total == 0) {
                    binding.ZuiJinDingDan.visibility = View.GONE  // 隐藏 View
                    binding.MeiYouGengDuo.visibility = View.GONE
                } else {
                    binding.ZuiJinDingDan.visibility = View.VISIBLE  // 重新显示 View
                    binding.MeiYouGengDuo.visibility = View.VISIBLE
                }
            }
        }


        binding.addressManage.setOnClickListener {
            val intent = Intent(this, AddressActivity::class.java)
            startActivity(intent)
        }

        binding.LiShiDingDan.setOnClickListener {
            val intent = Intent(this, liShiDingDanActivity::class.java)
            startActivity(intent)
        }

        binding.imFanHui.setOnClickListener {
            finish()
        }
    }

}