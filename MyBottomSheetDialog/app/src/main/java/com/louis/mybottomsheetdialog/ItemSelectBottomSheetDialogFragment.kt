package com.louis.mybottomsheetdialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.louis.mybottomsheetdialog.databinding.FragmentDialogBottomSheetItemSelectBinding


class ItemSelectBottomSheetDialogFragment : BottomSheetDialogFragment() {
    companion object {
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ItemSelectBottomSheetDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private var _binding: FragmentDialogBottomSheetItemSelectBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
//            param1 = it.getString(ARG_PARAM1)
//            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDialogBottomSheetItemSelectBinding.inflate(inflater, container, false)
        return binding.root

    }

    private var bottomSheetBehavior: BottomSheetBehavior<View>? = null
    override fun onStart() {
        super.onStart()
        val bottomSheetDialog = dialog as BottomSheetDialog? //as? 是安全转换，失败则返回 null
        if (bottomSheetDialog != null) {
            val view: View? =
                bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet)
            if (view != null) {
                bottomSheetBehavior = BottomSheetBehavior.from(view)
                //设置弹出高度
                bottomSheetBehavior?.peekHeight = 500
                bottomSheetBehavior?.isHideable = false
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvList.adapter =
            ItemRVAdapter(listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20"))

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * 展开面板（全屏）
     */
    private fun expandedBottomSheet() {
        if (bottomSheetBehavior == null) {
            return
        }
        bottomSheetBehavior?.setState(BottomSheetBehavior.STATE_EXPANDED)
    }

    /**
     * 隐藏面板
     */
    private fun hideBottomSheet() {
        if (bottomSheetBehavior == null) {
            return
        }
        bottomSheetBehavior?.setState(BottomSheetBehavior.STATE_HIDDEN)
    }

    /**
     * 收起面板（折叠）
     */
    private fun collapseBottomSheet() {
        if (bottomSheetBehavior == null) {
            return
        }
        bottomSheetBehavior?.setState(BottomSheetBehavior.STATE_COLLAPSED)
    }
}