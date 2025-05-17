package com.sunnyweather.changqiongwaimai.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import com.hjq.toast.Toaster
import com.sunnyweather.changqiongwaimai.R
import com.sunnyweather.changqiongwaimai.base.BaseActivity
import com.sunnyweather.changqiongwaimai.data.model.GlobalData
import com.sunnyweather.changqiongwaimai.data.model.Goods
import com.sunnyweather.changqiongwaimai.data.model.MenuItem
import com.sunnyweather.changqiongwaimai.data.repository.CategoryRepository
import com.sunnyweather.changqiongwaimai.ui.adapter.GoodsAdapter
import com.sunnyweather.changqiongwaimai.ui.adapter.MenuAdapter
import com.sunnyweather.changqiongwaimai.ui.fragment.FloatingCartFragment
import com.sunnyweather.changqiongwaimai.utils.PostViewModelFactory
import com.sunnyweather.changqiongwaimai.viewModel.CartViewModel
import com.sunnyweather.changqiongwaimai.viewModel.PostViewModel

/**
 * 程序主页面
 */
class MainActivity : BaseActivity() {

    private lateinit var menuRecyclerView: RecyclerView
    private lateinit var goodsRecyclerView: RecyclerView
    private lateinit var categoryRepository: CategoryRepository

    private lateinit var progressBar: ProgressBar
    private lateinit var tvNoDish: TextView
    private var lastBackTime = 0L


    private val cartViewModel: CartViewModel by viewModels()
    private val postViewModel: PostViewModel by viewModels {
        PostViewModelFactory(
            cartViewModel,
            categoryRepository
        )
    }

    // 声明全局变量保存 GoodsAdapter 实例
    private lateinit var goodsAdapter: GoodsAdapter
    private lateinit var menuAdapter: MenuAdapter

    //分类标题集合
    private val menuList = ArrayList<MenuItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //设置状态栏文字黑色
        ViewCompat.getWindowInsetsController(window.decorView)?.apply {
            isAppearanceLightStatusBars = true
        }

        //设置状态栏背景为白色
        window.statusBarColor = ContextCompat.getColor(this, R.color.white)

        // 一定要先请求购物车数据
        cartViewModel.getCart()

        //获取控件
        menuRecyclerView = findViewById(R.id.menuRecyclerView)      //分类recyclerview
        goodsRecyclerView = findViewById(R.id.goodsRecyclerView)    //商品recyclerview

        progressBar = findViewById(R.id.progressBar)
        tvNoDish = findViewById(R.id.tv_no_dish)

        //初始化Repository
        categoryRepository = CategoryRepository()
        //初始化分类集合
        addList()
        //初始化goodsRecycler
        setGoodsAdapter()

        // 此外，在观察 LiveData 数据变化时调用 updateGoodsList 方法来更新数据：
        postViewModel.uiList.observe(this) { list ->
            val goodsData = list
            //判断商品数据是否不为空
            if (!goodsData.isNullOrEmpty()) {
                //控制组件显示与隐藏
                loadGoodsSuccess()
                // 当数据非 null 且不为空时更新列表
                goodsAdapter.submitList(goodsData)
            } else {    //为空
                goodsRecyclerView.visibility = View.GONE
                findViewById<TextView>(R.id.tv_no_dish).visibility = View.VISIBLE
            }
        }

        // 设置分类菜单 RecyclerView
        menuRecyclerView.layoutManager = LinearLayoutManager(this)
        menuAdapter = MenuAdapter(this, menuList).apply {
            //发送请求 发送分类id获取商品数据
            onItemClick = { id ->
                loadGoods()
                postViewModel.fetchPosts(id)
            }
        }
        menuRecyclerView.adapter = menuAdapter

        //加载购物车fragment组件
        val fragment = FloatingCartFragment()  // 你可以换成不同的文字
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()

        //事件 个中心点
        val geRenZhongXin = findViewById<LinearLayout>(R.id.ll_user_go_my_activity)
        geRenZhongXin.setOnClickListener {
            val intent = Intent(this, MyActivity::class.java)
            startActivity(intent)
        }
    }

    //GoodRecyclerView 初始化
    private fun setGoodsAdapter() {
        //商品recycler垂直布局
        goodsRecyclerView.layoutManager = LinearLayoutManager(this)
        // 创建 GoodsAdapter 实例，并传入初始数据为空的 MutableList
        goodsAdapter = GoodsAdapter(
            context = this,
            goodsList = mutableListOf(), // 初始数据为空，后续通过 LiveData 更新数据
            onAddCartClicked = { goodsId: Int, selectedSpicyLevel: String ->
                // 当用户点击加入购物车时的逻辑处理：
                // 例如：调用协程请求将商品添加到购物车，并刷新相关数据
                cartViewModel.addToCart(
                    goodsId,
                )
                // 刷新购物车数据
                cartViewModel.getCart()
                // 根据需要刷新商品列表数据，这里调用 fetchPosts 并传入全局的分类ID
            },
            onIncrease = { goods: Goods ->
                // 当用户点击增加商品数量时的逻辑处理
                cartViewModel.addToCart(goods.id)
            },
            onDecrease = { goods: Goods ->
                // 当用户点击减少商品数量时的逻辑处理：
                cartViewModel.cartSubtractGoods(
                    goods.id,
                )
            }
        )
        //// 将创建好的 GoodsAdapter 设置给 RecyclerView
        goodsRecyclerView.adapter = goodsAdapter
    }

    // 初始化菜单数据
    fun addList() {
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

    //在第一次创建时和每次不可见变为可见时调用
    override fun onResume() {
        super.onResume()
        //查询购物车
        cartViewModel.getCart()
        //根据分类id查询商品
        postViewModel.fetchPosts(GlobalData.id)
    }

    /**
     * 加载loading
     */
    fun loadGoods() {
        progressBar.visibility = View.VISIBLE
        tvNoDish.visibility = View.GONE
        goodsRecyclerView.visibility = View.GONE
    }

    /**
     * 加载成功返回数据
     */
    fun loadGoodsSuccess() {
        progressBar.visibility = View.GONE
        tvNoDish.visibility = View.GONE
        goodsRecyclerView.visibility = View.VISIBLE
    }

    /**
     * 返回键回调
     */
    override fun onBackPressed() {
        val currentTime = System.currentTimeMillis()

        if (currentTime - lastBackTime < 2000) {
            super.onBackPressed()
        } else {
            Toaster.show("再按一次退出应用")
            lastBackTime = currentTime
        }
    }
}

