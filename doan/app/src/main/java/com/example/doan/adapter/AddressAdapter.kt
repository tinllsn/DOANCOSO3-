package com.example.doan.adapter

import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.doan.R
import com.example.doan.data.Address
import com.example.doan.databinding.AddressRvItemBinding

class AddressAdapter : Adapter<AddressAdapter.AddressViewHolder>() {

    inner class AddressViewHolder(val binding: AddressRvItemBinding) :
        ViewHolder(binding.root) {
        fun bind(address: Address,isSelected: Boolean) {
            binding.apply {
                buttonAddress.text = address.addressTitle
                if (isSelected){
                    buttonAddress.background = ColorDrawable(itemView.context.resources.getColor(R.color.g_blue))
                }else{
                    buttonAddress.background = ColorDrawable(itemView.context.resources.getColor(R.color.g_white))
                }
            }
        }


    }
//Nó dùng để so sánh các mục trong danh sách để biết xem có cần cập nhật RecyclerView hay không.
    private val diffUtil = object : DiffUtil.ItemCallback<Address>() {
//        areItemsTheSame kiểm tra nếu các mục có cùng tiêu đề và tên đầy đủ
        override fun areItemsTheSame(oldItem: Address, newItem: Address): Boolean {
            return oldItem.addressTitle == newItem.addressTitle && oldItem.fullName == newItem.fullName
        }
//   areContentsTheSame kiểm tra nếu hai mục có nội dung là giống nhau
        override fun areContentsTheSame(oldItem: Address, newItem: Address): Boolean {
            return oldItem == newItem
        }
    }
//     cập nhật RecyclerView một cách hiệu quả khi danh sách dữ liệu thay đổi.
    val differ = AsyncListDiffer(this, diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        return AddressViewHolder(
            AddressRvItemBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        )
    }

    var selectedAddress = -1
    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        val address = differ.currentList[position]
        holder.bind(address, selectedAddress == position)

        holder.binding.buttonAddress.setOnClickListener {
            if (selectedAddress >= 0)
                notifyItemChanged(selectedAddress)
            selectedAddress = holder.adapterPosition
            notifyItemChanged(selectedAddress)
            onClick?.invoke(address)
        }
    }
/* dùng ListListener để theo dõi khi danh sách thay đổi. Khi danh sách thay đổi,
 nó gọi notifyItemChanged(selectedAddress) để đảm bảo rằng mục đã chọn được cập nhật chính xác.*/
    init {
        differ.addListListener { _, _ ->
            notifyItemChanged(selectedAddress)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


    var onClick: ((Address) -> Unit)? = null
}
