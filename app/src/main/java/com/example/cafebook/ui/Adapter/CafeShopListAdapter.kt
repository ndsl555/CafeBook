package com.example.cafebook.ui.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.cafebook.Entity.CafeShopEntity
import com.example.cafebook.databinding.ItemCafeBinding

class CafeShopListAdapter : RecyclerView.Adapter<CafeShopListAdapter.CafeViewHolder>() {
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)

    var cafes: List<CafeShopEntity>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    var onCafeClick: ((CafeShopEntity) -> Unit)? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): CafeViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCafeBinding.inflate(inflater, parent, false)
        return CafeViewHolder(parent.context, binding)
    }

    override fun onBindViewHolder(
        holder: CafeViewHolder,
        position: Int,
    ) {
        holder.bind(cafes[position])
    }

    override fun getItemCount(): Int = cafes.size

    inner class CafeViewHolder(
        private val context: Context,
        private val binding: ItemCafeBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(cafe: CafeShopEntity) {
            binding.apply {
                // 名稱超過12字加換行
                val originalName = cafe.name
                val displayName =
                    if (originalName.length > 12) {
                        originalName.substring(0, 13) + "\n" + originalName.substring(13)
                    } else {
                        originalName
                    }
                textViewCafeName.text = displayName

                // 預設清空文字和背景
                SearchMrtTextView.text = ""
                SearchMrtTextView.background = null

                SearchTimeTextView.text = ""
                SearchTimeTextView.background = null

                SearchAveTextView.text = ""
                SearchAveTextView.background = null

                // 有捷運時顯示文字 "捷運"
                if (!cafe.mrt.isNullOrBlank()) {
                    SearchMrtTextView.text = "捷運"
                    // 你也可以設定背景，例如：
                    // SearchMrtTextView.setBackgroundColor(Color.GREEN)
                }

                // 限時判斷
                if (cafe.limitedTime == "yes") {
                    SearchTimeTextView.text = "限時"
                    // 同理可以設定背景色或圖片
                }

                val average =
                    listOf(
                        cafe.wifi,
                        cafe.seat,
                        cafe.quiet,
                        cafe.tasty,
                        cafe.cheap,
                        cafe.music,
                    ).average()
                SearchAveTextView.text = String.format("%.1f", average)
                root.setOnClickListener {
                    onCafeClick?.invoke(cafe)
                }
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK =
            object : DiffUtil.ItemCallback<CafeShopEntity>() {
                override fun areItemsTheSame(
                    oldItem: CafeShopEntity,
                    newItem: CafeShopEntity,
                ): Boolean {
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(
                    oldItem: CafeShopEntity,
                    newItem: CafeShopEntity,
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }
}
