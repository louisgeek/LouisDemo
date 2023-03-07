package com.louis.mybottomsheet

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.louis.mybottomsheet.databinding.FragmentItemListDialogListDialogItemBinding

class Item2Adapter constructor(private val mItemCount: Int, private val com.) :
    RecyclerView.Adapter<ItemListDialogFragment.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemListDialogFragment.ViewHolder {

        return com.louis.mybottomsheet.ViewHolder(
            FragmentItemListDialogListDialogItemBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )

    }

    override fun onBindViewHolder(holder: ItemListDialogFragment.ViewHolder, position: Int) {
        holder.text.text = position.toString()
    }

    override fun getItemCount(): Int {
        return mItemCount
    }
}