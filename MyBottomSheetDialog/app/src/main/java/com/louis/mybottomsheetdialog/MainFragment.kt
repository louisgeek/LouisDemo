package com.louis.mybottomsheetdialog

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

//    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        val tv: TextView = view.findViewById(R.id.tv)
        tv.setOnClickListener {
            val xxx = ItemSelectBottomSheetDialogFragment.newInstance("", "")
            xxx.show(getParentFragmentManager(), "MyBottomSheetDialog")
        }

    }

}