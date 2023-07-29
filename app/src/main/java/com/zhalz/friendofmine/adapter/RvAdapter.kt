package com.zhalz.friendofmine.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zhalz.friendofmine.database.FriendEntity
import com.zhalz.friendofmine.databinding.RvItemBinding

class RvAdapter(private val items: ArrayList<FriendEntity>, val onClickItem : (FriendEntity) -> Unit) :
    RecyclerView.Adapter<RvAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            RvItemBinding.inflate( LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.binding.nama = items[position].name
        holder.binding.sekolah = items[position].school

        holder.itemView.setOnClickListener { onClickItem(items[position]) }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ItemViewHolder(val binding: RvItemBinding) : RecyclerView.ViewHolder(binding.root)

}