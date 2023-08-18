package com.zhalz.friendofmine.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.zhalz.friendofmine.R
import com.zhalz.friendofmine.database.FriendEntity
import com.zhalz.friendofmine.databinding.ItemFriendBinding

class RvAdapter(
    var items: ArrayList<FriendEntity>,
    private val onItemClick : (FriendData : FriendEntity) -> Unit
) :
    RecyclerView.Adapter<RvAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_friend, parent, false))
//            RvItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(items[position])
        holder.itemView.setOnClickListener {
            onItemClick(items[position])
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ItemViewHolder(var itemFriendBinding: ItemFriendBinding): RecyclerView.ViewHolder(itemFriendBinding.root){
        fun bind(friendData: FriendEntity?){
            itemFriendBinding.friendData = friendData
            itemFriendBinding.executePendingBindings()
        }
    }
}