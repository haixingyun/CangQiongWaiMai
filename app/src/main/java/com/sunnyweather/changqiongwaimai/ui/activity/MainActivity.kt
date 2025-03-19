package com.sunnyweather.changqiongwaimai.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
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
import com.sunnyweather.changqiongwaimai.data.model.Goods
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

    // 声明全局变量保存 GoodsAdapter 实例
    private lateinit var goodsAdapter: GoodsAdapter

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

        //在 onCreate 中初始化 Adapter
        // 初始化 RecyclerView（假设 goodsRecyclerView 已在布局中定义）
        goodsRecyclerView = findViewById(R.id.goodsRecyclerView)
        goodsRecyclerView.layoutManager = LinearLayoutManager(this)

        // 创建 GoodsAdapter 实例，并传入初始数据为空的 MutableList
        goodsAdapter = GoodsAdapter(
            context = this,
            goodsList = mutableListOf(), // 初始数据为空，后续通过 LiveData 更新数据
            onAddCartClicked = { goodsId: Int, selectedSpicyLevel: String ->
                // 当用户点击加入购物车时的逻辑处理：
                // 例如：调用协程请求将商品添加到购物车，并刷新相关数据
                lifecycleScope.launch {
                    // 调用 CartRepository 中的方法添加到购物车
                    cartRepository.addToCart(Flavor(goodsId, selectedSpicyLevel))
                    // 刷新购物车数据
                    cartViewModel.getCart()
                    // 根据需要刷新商品列表数据，这里调用 fetchPosts 并传入全局的分类ID
                    postViewModel.fetchPosts(GlobalData.id)
                }
            },
            onIncrease = { goods: Goods ->
                // 同时更新 ViewModel 中保存的商品数量
                postViewModel.incrementQuantity(goods)
                // 当用户点击增加商品数量时的逻辑处理：
                lifecycleScope.launch {
                    // 调用 CartRepository 添加到购物车（根据实际业务调整参数）
                    val success = cartRepository.addToCart(Flavor(goods.id))
                    if (success) {
                        // 成功后刷新购物车和商品列表数据
                        cartViewModel.getCart()
                    } else {
                        Log.e("CartNetwork", "添加购物车失败")
                    }
                }
            },
            onDecrease = { goods: Goods ->
                // 更新 ViewModel 中商品的数量
                postViewModel.decrementQuantity(goods)
                // 当用户点击减少商品数量时的逻辑处理：
                lifecycleScope.launch {
                    // 调用 CartRepository 减少购物车中的商品数量
                    val result = cartRepository.cartSubtractGoods(Flavor(goods.id, null))
                    if (result.code == 1) {
                        // 成功后刷新购物车数据和商品列表数据
                        cartViewModel.getCart()
                    } else {
                        Log.e("CartNetwork", "减少购物车商品失败：${result.msg}")
                    }
                }
            }
        )

        // 将创建好的 GoodsAdapter 设置给 RecyclerView
        goodsRecyclerView.adapter = goodsAdapter

        // 此外，在观察 LiveData 数据变化时调用 updateGoodsList 方法来更新数据：
        postViewModel.posts.observe(this) { result ->
            val goodsData = result.data
            if (!goodsData.isNullOrEmpty()) {
                findViewById<RecyclerView>(R.id.goodsRecyclerView).visibility = View.VISIBLE
                findViewById<TextView>(R.id.tv_no_dish).visibility = View.GONE
                // 当数据非 null 且不为空时更新列表
                goodsAdapter.submitList(goodsData)
            } else {
                // 当数据为 null 或空集合时，执行另一个条件的逻辑
                findViewById<RecyclerView>(R.id.goodsRecyclerView).visibility = View.GONE
                findViewById<TextView>(R.id.tv_no_dish).visibility = View.VISIBLE
            }
        }

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
        menuList.add(MenuItem("酒水饮料", 11))
        menuList.add(MenuItem("本店特色", 10))
        menuList.add(MenuItem("传统主食", 12))
        menuList.add(MenuItem("人气套餐", 13))
        menuList.add(MenuItem("商务套餐", 15))
        menuList.add(MenuItem("蜀味烤鱼", 16))
        menuList.add(MenuItem("蜀味牛蛙", 17))
        menuList.add(MenuItem("特色蒸菜", 18))
        menuList.add(MenuItem("新鲜时蔬", 19))
        menuList.add(MenuItem("水煮鱼", 20))
        menuList.add(MenuItem("汤类", 21))
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

