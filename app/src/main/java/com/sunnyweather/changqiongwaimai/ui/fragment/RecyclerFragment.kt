package com.sunnyweather.changqiongwaimai.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hjq.toast.Toaster
import com.sunnyweather.changqiongwaimai.data.repository.CartRepository
import com.sunnyweather.changqiongwaimai.data.repository.OrderRepository
import com.sunnyweather.changqiongwaimai.databinding.FragmentListBinding
import com.sunnyweather.changqiongwaimai.ui.activity.OrderDetailActivity
import com.sunnyweather.changqiongwaimai.ui.adapter.OrderAdapter
import com.sunnyweather.changqiongwaimai.viewModel.OrderViewModel
import kotlinx.coroutines.launch

class RecyclerFragment : Fragment() {

    //    companion object：这是 Kotlin 里用来定义类级别的成员的方式。companion object 中的成员可直接通过类名访问，不用创建类的实例。
    //    @JvmStatic：这个注解的作用是让 newInstance 方法在 Java 代码里能像静态方法一样被调用。在 Kotlin 中，companion object 里的方法默认不是静态的，
    //    不过借助 @JvmStatic 注解就能在 Java 代码里以静态方法的形式调用。
    companion object {
        private const val ARG_ORDER_STATUS = "order_status"
        fun newInstance(orderStatus: String): RecyclerFragment {
            val fragment = RecyclerFragment()
            val bundle = Bundle()
            Log.d("111", orderStatus)
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
    // 声明 orderStatus，但推迟初始化
    private lateinit var orderStatus: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let { // 安全地访问 arguments
            orderStatus = it.getString(ARG_ORDER_STATUS) ?: "全部订单" // 获取参数，提供默认值以防万一
            Log.d("RecyclerFragment", "onCreate: Received orderStatus = $orderStatus") // 添加日志确认
        } ?: run {
            orderStatus = "全部订单" // 这种情况理论上不应该发生，但做好防御
            Log.e("RecyclerFragment", "onCreate: Arguments were unexpectedly null!")
        }

        cartRepository = CartRepository()
        orderRepository = OrderRepository()

    }

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


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 现在 'orderStatus' 变量包含了从 MyActivity 正确传递过来的值
        Log.d("RecyclerFragment", "onViewCreated: Using orderStatus = $orderStatus to get orders") // 添加日志确认

        ordersRecyclerView = binding.recyclerView
        ordersRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        if (orderViewModel.post.value == null) {
            orderViewModel.getOrders(orderStatus)
        }

        //请求查询最近订单数据
        orderViewModel.getOrders(orderStatus)

        //监听最近订单详情数据
        orderViewModel.post.observe(viewLifecycleOwner) { result ->
            result?.records?.let { orderData ->
                ordersRecyclerView.adapter = OrderAdapter(requireContext(), orderData,
                    zaiLaiYiDan = { orderId ->
                        lifecycleScope.launch {
                            cartRepository.emptyCart()   //清空购物车
                            orderRepository.ZaiLaiYiDan(orderId)  //再来一单
                            cartRepository.getCategoryDish() //查询分类数据
                            //在 Android 中要销毁当前 Fragment 和所在 Activity 并返回上一个 Activity
                            requireActivity().finish()
                        }
                    },
                    orderDetail = { orderId ->
                        val intent = Intent(requireContext(), OrderDetailActivity::class.java)
                        intent.putExtra("order_id", orderId)
                        startActivity(intent)
                    },
                    //催单回调
                    CuiDan = { orderId ->
                        lifecycleScope.launch {
                            //发送催单请求
                            orderRepository.CuiDan(orderId)
                        }
                        Toaster.showLong("催单成功")
                    },
                    //去支付按钮回调
                    goPay = { orderId ->
                        val intent = Intent(requireContext(), OrderDetailActivity::class.java)
                        intent.putExtra("order_id", orderId)  // 添加数据，orderId 为你想传递的值
                        startActivity(intent)
                    }
                )
            }
        }
    }
}