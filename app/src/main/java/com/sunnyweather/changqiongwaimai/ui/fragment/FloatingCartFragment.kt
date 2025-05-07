package com.sunnyweather.changqiongwaimai.ui.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.sunnyweather.changqiongwaimai.R
import com.sunnyweather.changqiongwaimai.databinding.FragmentCartBinding
import com.sunnyweather.changqiongwaimai.ui.activity.SubmitOrderActivity
import com.sunnyweather.changqiongwaimai.viewModel.CartViewModel

class FloatingCartFragment : Fragment() {

    // 定义回调接口
    interface OnButtonClickListener {
        fun onButtonClicked()
    }

    private var callback: OnButtonClickListener? = null

    private lateinit var binding: FragmentCartBinding
    private val cartViewModel: CartViewModel by activityViewModels()

    companion object {
        private const val ARG_CHECKOUT_TEXT = "arg_checkout_text"

        // 创建 FloatingCartFragment 的静态方法
        fun newInstance(checkoutText: String): FloatingCartFragment {
            val fragment = FloatingCartFragment()
            val args = Bundle()
            args.putString(ARG_CHECKOUT_TEXT, checkoutText)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cardView = binding.floatingCart  //获取购物车组件
        val goodsNumber = binding.cartBadge  //获取商品数量控件
        val cartTotalPrice = binding.cartTotalPrice  //获取总价控件
        val cartIcon = binding.cartIcon
        val cartCheckout = binding.cartCheckout

        // 获取传递的 checkout 按钮文本
        val checkoutText = arguments?.getString(ARG_CHECKOUT_TEXT) ?: "去结算"
        cartCheckout.text = checkoutText

        //事件去结算按钮
        binding.cartCheckout.setOnClickListener {
            if (checkoutText == "去结算") {
                val intent = Intent(requireContext(), SubmitOrderActivity::class.java)
                startActivity(intent)
            } else {
                // 通过接口回调通知 Activity
                callback?.onButtonClicked()
            }
        }

        //监听购物车数据
        cartViewModel.posts.observe(viewLifecycleOwner) { posts ->
            if (posts.isNullOrEmpty()) {
                goodsNumber.text = 0.toString()
                cartTotalPrice.text = 0.toString()
                cartIcon.setImageResource(R.drawable.waimai_0)
                cartCheckout.visibility = View.GONE
                cardView.isClickable = false  //不能点击
            } else {
                cardView.isClickable = true
                cartCheckout.visibility = View.VISIBLE
                cartIcon.setImageResource(R.drawable.waimai)
                val totalNumber = posts.sumOf { it.number }
                val totalPrice = posts.sumOf { it.amount * it.number }
                goodsNumber.text = totalNumber.toString()
                cartTotalPrice.text = "￥$totalPrice"
            }
        }

        cardView.setOnClickListener {
            val cartFragment = CartBottomSheetFragment()
            cartFragment.show(parentFragmentManager, "CartBottomSheet")
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnButtonClickListener) {
            callback = context
        } else {
            // 如果宿主 Activity 没有实现接口，不抛出异常
            null
        }
    }
}