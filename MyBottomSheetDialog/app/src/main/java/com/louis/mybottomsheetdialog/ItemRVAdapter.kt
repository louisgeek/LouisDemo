package com.louis.mybottomsheetdialog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.radiobutton.MaterialRadioButton

class ItemRVAdapter(private val dataList: List<String>) : RecyclerView.Adapter<ItemRVAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_select, parent, false)
        return ItemViewHolder(itemView)

    }

    override fun getItemCount() = dataList.size

    override fun onBindViewHolder(viewHolder: ItemViewHolder, position: Int) {
        viewHolder.tvName.text = dataList[position]
    }


    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val rbSelect: MaterialRadioButton = itemView.findViewById(R.id.rbSelect)
    }

}