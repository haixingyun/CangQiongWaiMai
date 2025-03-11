package com.sunnyweather.changqiongwaimai.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.lifecycle.lifecycleScope
import com.sunnyweather.changqiongwaimai.R
import com.sunnyweather.changqiongwaimai.data.model.Flavor
import com.sunnyweather.changqiongwaimai.data.model.GlobalData
import com.sunnyweather.changqiongwaimai.data.model.MenuItem
import com.sunnyweather.changqiongwaimai.data.repository.CartRepository
import com.sunnyweather.changqiongwaimai.data.repository.CategoryRepository
import com.sunnyweather.changqiongwaimai.ui.adapter.GoodsAdapter
import com.sunnyweather.changqiongwaimai.ui.adapter.MenuAdapter
import com.sunnyweather.changqiongwaimai.ui.fragment.FloatingCartFragment
import com.sunnyweather.changqiongwaimai.viewModel.CartViewModel
import com.sunnyweather.changqiongwaimai.viewModel.PostViewModel
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var menuRecyclerView: RecyclerView
    private lateinit var goodsRecyclerView: RecyclerView
    private lateinit var menuAdapter: MenuAdapter
    private lateinit var categoryRepository: CategoryRepository

    private val postViewModel: PostViewModel by viewModels()
    private val cartViewModel: CartViewModel by viewModels()
    private lateinit var cartRepository: CartRepository

    private val menuList = ArrayList<MenuItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        或者使用 Jetpack WindowInsetsControllerCompat（推荐）
        ViewCompat.getWindowInsetsController(window.decorView)?.apply {
            isAppearanceLightStatusBars = true // 文字黑色
        }

        window.statusBarColor = ContextCompat.getColor(this, R.color.white)


        cartRepository = CartRepository(/* 如果需要参数，则传入相应参数 */)
        categoryRepository = CategoryRepository()

        menuRecyclerView = findViewById(R.id.menuRecyclerView)
        goodsRecyclerView = findViewById(R.id.goodsRecyclerView)


        //初始化分类集合
        addList()

        // 设置菜单 RecyclerView
        menuRecyclerView.layoutManager = LinearLayoutManager(this)
        menuAdapter = MenuAdapter(this, menuList).apply {
            //发送请求 发送分类id获取商品数据
            onItemClick = { id ->
                postViewModel.fetchPosts(id)
            }
        }

        menuRecyclerView.adapter = menuAdapter
        // 设置商品 RecyclerView的排序方式
        goodsRecyclerView.layoutManager = LinearLayoutManager(this)

        //监听分类商品数据
        postViewModel.posts.observe(this) { result ->
            result.data?.let { goodsData ->
                goodsRecyclerView.adapter = GoodsAdapter(
                    context = this,
                    goodsList = goodsData,
                    //点击添加商品回调回调
                    onAddCartClicked = { goodsId, selectedSpicyLevel ->
                        lifecycleScope.launch {
                            cartRepository.addToCart(Flavor(goodsId, selectedSpicyLevel))
                            cartViewModel.getCart()
                            postViewModel.fetchPosts(GlobalData.id)
                        }
                    },
                    //点击添加商品数量回调回调
                    onIncrease = { goodsId ->
                        lifecycleScope.launch {
                            val cartSubtractGoods =
                                cartRepository.addToCart(Flavor(goodsId))
                            if (cartSubtractGoods) {
                                //重新查询购物车
                                cartViewModel.getCart()
                                //重新查询分类商品
                                postViewModel.fetchPosts(GlobalData.id)
                            } else {
                                Log.e("CartNetwork", "购物车减少商品失败$")
                            }
                        }
                    },
                    //点击减少商品数量回调回调
                    onDecrease = { goodsId ->
                        lifecycleScope.launch {
                            val cartSubtractGoods =
                                cartRepository.cartSubtractGoods(Flavor(goodsId, null))
                            if (cartSubtractGoods.code == 1) {
                                //重新查询购物车
                                cartViewModel.getCart()
                                //重新查询分类商品
                                postViewModel.fetchPosts(GlobalData.id)
                            } else {
                                Log.e("CartNetwork", "购物车减少商品失败${cartSubtractGoods.msg}")
                            }
                        }
                    }
                )
            }
        }

        // 监听请求失败
        postViewModel.error.observe(this) { error ->
            error?.takeIf { it.isNotEmpty() }?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }

        //加载购物车fragment组件
        val fragment = FloatingCartFragment()  // 你可以换成不同的文字
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()

        //监听个中心点击事件
        val geRenZhongXin = findViewById<LinearLayout>(R.id.iv_GeRenZhongXin)
        geRenZhongXin.setOnClickListener {
            val intent = Intent(this, MyActivity::class.java)
            startActivity(intent)
        }

    }

    fun addList() {
        // 初始化菜单数据
        menuList.add(MenuItem("蜀味烤鱼", 17))
        menuList.add(MenuItem("蜀味牛蛙", 18))
        menuList.add(MenuItem("特色蒸菜", 19))
        menuList.add(MenuItem("新鲜时蔬", 15))
        menuList.add(MenuItem("水煮鱼", 20))
        menuList.add(MenuItem("传统主食", 10))
        menuList.add(MenuItem("酒水饮料", 8))
        menuList.add(MenuItem("汤类", 12))
        menuList.add(MenuItem("人气套餐", 25))
        menuList.add(MenuItem("商务套餐", 30))
        menuList.add(MenuItem("商务套餐", 30))
        menuList.add(MenuItem("商务套餐", 30))
        menuList.add(MenuItem("商务套餐", 30))
        menuList.add(MenuItem("商务套餐", 30))
        menuList.add(MenuItem("商务套餐", 30))
        menuList.add(MenuItem("商务套餐", 30))
        menuList.add(MenuItem("商务套餐", 30))
        menuList.add(MenuItem("商务套餐", 30))
        menuList.add(MenuItem("商务套餐", 30))
        menuList.add(MenuItem("商务套餐", 30))
    }

    override fun onResume() {
        super.onResume()
        //查询购物车
        cartViewModel.getCart()
        lifecycleScope.launch {
            postViewModel.fetchPosts(GlobalData.id)
        }

    }
}

