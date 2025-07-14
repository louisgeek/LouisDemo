package com.louis.mybrvah

import android.content.Context
import android.view.ViewGroup
import android.widget.TextView
import com.chad.library.adapter4.BaseQuickAdapter
import com.chad.library.adapter4.util.getItemView
import com.chad.library.adapter4.viewholder.QuickViewHolder

class MyBaseAdapter : BaseQuickAdapter<String, QuickViewHolder>() {
    override fun onBindViewHolder(holder: QuickViewHolder, position: Int, item: String?) {
//        holder.setText(R.id.tv01, "sss $position $item")
        holder.getView<TextView>(R.id.tv01).text = "qqq $position $item"
    }

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): QuickViewHolder {
//        parent.getItemView(R.layout.list_item_01)
        return QuickViewHolder(R.layout.list_item_01, parent)
    }
}