package com.sunnyweather.changqiongwaimai.data.api

import com.sunnyweather.changqiongwaimai.data.model.Address
import com.sunnyweather.changqiongwaimai.data.model.AddressRequest
import com.sunnyweather.changqiongwaimai.data.model.Dish
import com.sunnyweather.changqiongwaimai.data.model.Flavor
import com.sunnyweather.changqiongwaimai.data.model.Goods
import com.sunnyweather.changqiongwaimai.data.model.Order
import com.sunnyweather.changqiongwaimai.data.model.OrderEntity
import com.sunnyweather.changqiongwaimai.data.model.OrderPayment
import com.sunnyweather.changqiongwaimai.data.model.OrderResultData
import com.sunnyweather.changqiongwaimai.data.model.Orders
import com.sunnyweather.changqiongwaimai.data.model.ResponseData
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    /**
     * 根据分类id查询菜品
     */
    @GET("dish/list") // TODO 修改为你的 API 地址
    suspend fun getGoodsData(@Query("categoryId") categoryId: Int): ResponseData<List<Goods>>

    /**
     * 把商品添加到购物车
     */
    @POST("shoppingCart/add")
    suspend fun addCartGood(@Body flavor: Flavor): ResponseData<*>

    /**
     * 查询购物车数据
     */
    @GET("shoppingCart/list")
    suspend fun getCartGood(): ResponseData<List<Dish>>

    /**
     * 减少商品购物车
     */
    @POST("shoppingCart/sub")
    suspend fun subtractCartGood(@Body flavor: Flavor): ResponseData<*>

    /**
     * 清空购物车数据
     */
    @DELETE("shoppingCart/clean")
    suspend fun emptyCartGoods(): ResponseData<*>

    /**
     * 查询最近订单数据
     */
    @GET("order/historyOrders?pageSize=10&page=1")
    suspend fun getOrders(): ResponseData<Orders>

    /**
     * 再来一单
     */
    @POST("order/repetition/{id}")
    suspend fun ZaiLaiYiDan(@Path("id") id: Int): ResponseData<*>

    /**
     * 查询所有地址
     */
    @GET("addressBook/list")
    suspend fun getAddressList(): ResponseData<List<Address>>

    /**
     * 设置为默认地址
     */
    @PUT("addressBook/default")
    suspend fun settingDefaultAddress(@Body addressRequest: AddressRequest): ResponseData<*>

    /**
     * 编辑收获地址
     */
    @GET("addressBook/{id}")
    suspend fun editAddressBook(
        @Path("id") id: Int,
        @Query("id") queryId: Int       // 作为查询参数
    ): ResponseData<Address>  // 假设返回的是 Address 类型的响应

    /**
     * 修改收获地址
     */
    @PUT("addressBook")
    suspend fun editAddressBook(@Body address: Address): ResponseData<*>

    /**
     * 根据id删除收获地址
     */
    @DELETE("addressBook")
    suspend fun deleteAddressBook(
        @Query("id") id: Int,
    ): ResponseData<*>

    /**
     * 修改收获地址
     */
    @POST("addressBook")
    suspend fun saveAddressBook(@Body address: Address): ResponseData<*>

    /**
     * 根据订单id查看订单详情
     */
    @GET("order/orderDetail/{id}")
    suspend fun getOrderDetails(@Path("id") id: Int): ResponseData<Order>

    /**
     * 查询订单数据
     */
    @GET("order/historyOrders")
    suspend fun getAllOrders(
        @Query("pageSize") pageSize: Int = 10,
        @Query("page") page: Int = 1,
        @Query("status") status: String = ""
    ): ResponseData<Orders>

    /**
     * 查询默认收获地址
     */
    @GET("addressBook/default")
    suspend fun getDefaultAddress(): ResponseData<Address?>

    /**
     * 提交订单
     */
    @POST("order/submit")
    suspend fun submitOrder(@Body orderEntity: OrderEntity): ResponseData<OrderResultData>

    /**
     * 支付订单
     */
    @PUT("order/payment")
    suspend fun paymentOrder(@Body orderPayment: OrderPayment): ResponseData<*>

    /**
     * 催单
     */
    @GET("order/reminder/{orderId}")
    suspend fun CuiDan(@Path("orderId") orderId: Int) : ResponseData<*>

    /**
     * 取消订单
     */
    @PUT("order/cancel/{orderId}")
    suspend fun cancelOrder(@Path("orderId") orderId: Int) : ResponseData<*>
}