package com.sunnyweather.changqiongwaimai.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.sunnyweather.changqiongwaimai.data.repository.CartRepository
import com.sunnyweather.changqiongwaimai.data.repository.OrderRepository
import com.sunnyweather.changqiongwaimai.databinding.FragmentListBinding
import com.sunnyweather.changqiongwaimai.ui.activity.MainActivity
import com.sunnyweather.changqiongwaimai.ui.activity.OrDerDetailActivity
import com.sunnyweather.changqiongwaimai.ui.adapter.OrderAdapters
import com.sunnyweather.changqiongwaimai.viewModel.OrderViewModel
import kotlinx.coroutines.launch

class RecyclerFragment : Fragment() {

    companion object {
        private const val ARG_ORDER_STATUS = "order_status"
        fun newInstance(orderStatus: String): RecyclerFragment {
            val fragment = RecyclerFragment()
            val bundle = Bundle()
            bundle.putString(ARG_ORDER_STATUS, orderStatus)
            fragment.arguments = bundle
            return fragment
        }
    }

    private lateinit var ordersRecyclerView: RecyclerView
    private lateinit var binding: FragmentListBinding
    private lateinit var cartRepository: CartRepository
    private lateinit var orderRepository: OrderRepository
    private val orderViewModel: OrderViewModel by viewModels()
    val orderStatus = arguments?.getString(ARG_ORDER_STATUS) ?: "全部订单"  //默认加载全部订单         // 获取传入的订单状态参数

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //使用数据绑定来绑定布局
        binding = FragmentListBinding.inflate(inflater, container, false)
        //设置RecyclerView
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        cartRepository = CartRepository()
        orderRepository = OrderRepository()


    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        //主要改动：
        //observe(viewLifecycleOwner)：使用 viewLifecycleOwner 来观察 LiveData。
        //requireContext()：使用 requireContext() 来获取上下文传递给 Toast 和 Intent。
        //lifecycleScope.launch：确保使用 viewLifecycleOwner.lifecycleScope 来启动协程，确保它与视图的生命周期绑定。

        ordersRecyclerView = binding.recyclerView
        ordersRecyclerView.layoutManager = LinearLayoutManager(requireContext())



        //请求查询最近订单数据
        orderViewModel.getOrders(orderStatus)

        //监听最近订单详情数据
        orderViewModel.post.observe(viewLifecycleOwner) { result ->
            result?.records?.let { orderData ->
                ordersRecyclerView.adapter = OrderAdapters(requireContext(), orderData,
                    zaiLaiYiDan = { orderId ->
                        Toast.makeText(requireContext(), "点击了再来一单", Toast.LENGTH_SHORT)
                            .show()
                        lifecycleScope.launch {
                            cartRepository.emptyCart()   //清空购物车
                            orderRepository.ZaiLaiYiDan(orderId)  //再来一单
                            cartRepository.getCategoryDish() //查询分类数据
                            val intent = Intent(requireContext(), MainActivity::class.java)
                            startActivity(intent)
                        }
                    },
                    orderDetail = { orderId ->
                        Toast.makeText(requireContext(), "点击了订单详情", Toast.LENGTH_SHORT)
                            .show()
                        val intent = Intent(requireContext(), OrDerDetailActivity::class.java)
                        intent.putExtra("order_id", orderId)
                        startActivity(intent)
                    },
                    //催单回调
                    CuiDan = { orderId ->
                        lifecycleScope.launch {
                            //发送催单请求
                            orderRepository.CuiDan(orderId)
                        }
                        MaterialDialog(requireContext()).show {
                            title(text = "提示")
                            message(text = "催单成功")
                            positiveButton(text = "确定")
                            negativeButton(text = "取消")
                        }
                    },
                    //去支付按钮回调
                    goPay = { orderId ->
                        val intent = Intent(requireContext(), OrDerDetailActivity::class.java)
                        intent.putExtra("order_id", orderId)  // 添加数据，orderId 为你想传递的值
                        startActivity(intent)
                    }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        orderViewModel.getOrders(orderStatus)
    }
}