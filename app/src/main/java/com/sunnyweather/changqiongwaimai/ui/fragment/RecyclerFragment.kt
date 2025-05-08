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

/**
 *  展示订单的fragment
 */
class RecyclerFragment : Fragment() {

    companion object {
        private const val ARG_ORDER_STATUS = "order_status"
        @JvmStatic
        fun newInstance(orderStatus: String): RecyclerFragment {
            val fragment = RecyclerFragment()
            val bundle = Bundle()
            Log.d("111", orderStatus)
            bundle.putString(ARG_ORDER_STATUS, orderStatus)
            fragment.arguments = bundle
            return fragment
        }
    }

    private lateinit var binding: FragmentListBinding
    private lateinit var ordersRecyclerView: RecyclerView
    private lateinit var orderAdapter: OrderAdapter
    private lateinit var cartRepository: CartRepository
    private lateinit var orderRepository: OrderRepository
    private val orderViewModel: OrderViewModel by viewModels()

    //订单状态
    private lateinit var orderStatus: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //获取传来的订单状，没传默认是全部订单
        orderStatus = arguments?.getString(ARG_ORDER_STATUS) ?: "全部订单"

        //初始化Repository
        cartRepository = CartRepository()
        orderRepository = OrderRepository()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ordersRecyclerView = binding.recyclerView

        //请求查询最近订单数据
        orderViewModel.getOrders(orderStatus)

        //初始化recycler
        initRecyclerView()

        //监听最近订单详情数据
        orderViewModel.post.observe(viewLifecycleOwner) { result ->
            result?.records?.let { orderData ->
                orderAdapter.submitList(orderData)
            }
        }
    }

    /**
     * 初始化recycler
     */
    fun initRecyclerView() {
        //设置recycler的排列方式，不设置不显示
        ordersRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        orderAdapter = OrderAdapter(requireContext(), mutableListOf(),
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
        ordersRecyclerView.adapter = orderAdapter
    }
}