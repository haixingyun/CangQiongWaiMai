package com.sunnyweather.changqiongwaimai.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sunnyweather.changqiongwaimai.R
import com.sunnyweather.changqiongwaimai.data.model.Flavor
import com.sunnyweather.changqiongwaimai.data.model.GlobalData
import com.sunnyweather.changqiongwaimai.data.repository.CartRepository
import com.sunnyweather.changqiongwaimai.databinding.FragmentCartBottomSheetBinding
import com.sunnyweather.changqiongwaimai.ui.adapter.CartAdapter
import com.sunnyweather.changqiongwaimai.viewModel.CartViewModel
import com.sunnyweather.changqiongwaimai.viewModel.PostViewModel
import kotlinx.coroutines.launch

/**
 * 点击购物车底部弹出框
 */
class CartBottomSheetFragment(
) : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentCartBottomSheetBinding    // 绑定视图
    private lateinit var goodsRecyclerView: RecyclerView
    private lateinit var cartRepository: CartRepository
    private val postViewModel: PostViewModel by viewModels()
    // 修改 Fragment 中的 ViewModel 声明
    private val cartViewModel: CartViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCartBottomSheetBinding.inflate(inflater, container, false)
        cartRepository = CartRepository() // 根据你的实现方式初始化
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        goodsRecyclerView = view.findViewById(R.id.recyclerView)

        cartViewModel.getCart()

        goodsRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        val goodsEmpty: LinearLayout = binding.qingKong

        //点击清空事件
        goodsEmpty.setOnClickListener {
            lifecycleScope.launch {
                val response = cartRepository.emptyCart()
                if (response.code == 1) {
                    Log.d("CartNetWork", "清空购物车成功")
                    // 强制清空 ViewModel 中的数据
                    // 重新查询购物车（确保数据一致）
                    cartViewModel.getCart()
                    postViewModel.fetchPosts(GlobalData.id)
                    dismiss()
                } else {
                    Log.e("CartNetwork", "清空购物车失败：${response.msg}")
                }
            }
        }

        //viewModel监听购物车商品数据
        cartViewModel.posts.observe(viewLifecycleOwner) { posts ->
            posts?.let {
                // 这里可以初始化购物车数据
                binding.recyclerView.adapter = CartAdapter(
                    requireContext(), posts,
                    //添加按钮回调
                    onIncrease = { goodsId ->
                        cartViewModel.addToCart(goodsId)
                    },
                    //减少按钮回调
                    onDecrease = { goodsId ->
                        cartViewModel.cartSubtractGoods(goodsId)
                    }
                )
            }
        }
    }
}
