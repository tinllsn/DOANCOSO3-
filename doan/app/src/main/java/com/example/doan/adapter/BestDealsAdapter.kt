package com.example.doan.adapter

import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.doan.data.Product
import com.example.doan.databinding.BestDealsRvItemBinding

class BestDealsAdapter : RecyclerView.Adapter<BestDealsAdapter.BestDealsViewHolder>() {

    inner class BestDealsViewHolder(private val binding: BestDealsRvItemBinding) :
//     giữ và quản lý các thành phần giao diện người dùng trong một mục của RecyclerView
        RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product) {
//             glide dùng để tải hình ảnh
            binding.apply {
//                Glide.with(itemView) bắt đầu quá trình tải hình ảnh, và itemView là itemView của ViewHolder.
//                .load(product.images[0]) chỉ định URL của hình ảnh để tải. product.images[0] là đường dẫn của hình ảnh đầu tiên trong danh sách hình ảnh của sản phẩm
//                 .into(imgBestDeal) chỉ định ImageView (imgBestDeal) mà hình ảnh sẽ được hiển thị vào
                Glide.with(itemView).load(product.images[0]).into(imgBestDeal)
                product.offerPercentage?.let {
                    val remainingPricePercentage = 1f - it
                    val priceAfterOffer = remainingPricePercentage * product.price
                    tvNewPrice.text = "$ ${String.format("%.2f",priceAfterOffer)}"
                    tvOldPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                }

                tvOldPrice.text = "$ ${product.price}"
                tvDealProductName.text = product.name
            }
        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id

        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BestDealsViewHolder {
        return BestDealsViewHolder(
            BestDealsRvItemBinding.inflate(
                LayoutInflater.from(parent.context),parent,false
            )
        )
    }

    override fun onBindViewHolder(holder: BestDealsViewHolder, position: Int) {
        val product = differ.currentList[position]
        holder.bind(product)

        holder.itemView.setOnClickListener {
            onClick?.invoke(product)
        }

    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    var onClick: ((Product) -> Unit)? = null
}













