package com.sunnyweather.changqiongwaimai.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.sunnyweather.changqiongwaimai.databinding.TiJiaoChengGongFragmentBinding
import com.sunnyweather.changqiongwaimai.ui.activity.MainActivity
import com.sunnyweather.changqiongwaimai.ui.activity.OrDerDetailActivity
import com.sunnyweather.changqiongwaimai.viewModel.OrderViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class tiJiaoChengGongFragment : DialogFragment() {


    private lateinit var binding: TiJiaoChengGongFragmentBinding
    private val orderViewModel: OrderViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = TiJiaoChengGongFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val estimatedTime = LocalDateTime.now().plusHours(1)
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        val formattedTime = estimatedTime.format(formatter)
        binding.YuJiShiJian.text = formattedTime



        binding.FanHuiShouYe.setOnClickListener {
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
            requireActivity().finish() // 销毁宿主 activity
            dismiss()
        }

        binding.ChaKanDingDan.setOnClickListener {
            val intent = Intent(requireContext(), OrDerDetailActivity::class.java)
            intent.putExtra("order_id", orderViewModel.orderId.value)
            startActivity(intent)
            requireActivity().finish()
            dismiss()
        }
    }


    //全屏
    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )  // 设置全屏
            setBackgroundDrawableResource(android.R.color.transparent)  // 透明背景，避免有圆角
        }
    }

}
