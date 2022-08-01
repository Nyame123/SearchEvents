package com.bismark.searchevents.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bismark.searchevents.R
import com.bismark.searchevents.databinding.HeaderLayoutBinding
import com.bismark.searchevents.databinding.SearchItemLayoutBinding
import com.bismark.searchevents.ui.SearchEventState.SearchItem

const val HEADER_LAYOUT = R.layout.header_layout
const val ITEM_LAYOUT = R.layout.search_item_layout

class SearchEventAdapter :
    ListAdapter<SearchEventState, RecyclerView.ViewHolder>(SEARCH_EVENT_DIFF) {

    companion object {

        private val SEARCH_EVENT_DIFF = object : DiffUtil.ItemCallback<SearchEventState>() {

            override fun areItemsTheSame(oldItem: SearchEventState, newItem: SearchEventState): Boolean {
                return when {
                    oldItem is SearchEventState.SearchHeader && newItem is SearchEventState.SearchHeader ->
                        oldItem.id == newItem.id
                    oldItem is SearchItem && newItem is SearchItem ->
                        oldItem.id == newItem.id
                    else -> false
                }
            }

            override fun areContentsTheSame(oldItem: SearchEventState, newItem: SearchEventState): Boolean {
                return oldItem == newItem
            }

        }
    }

    inner class ViewHolder(private val binding: SearchItemLayoutBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SearchItem) {
            val resources = binding.root.context.resources
            binding.cityTv.text = resources.getString(EventString.city, item.city)
            binding.venueTv.text = resources.getString(EventString.venue, item.venue)
            binding.priceTv.text = resources.getString(EventString.price, item.price.toString())
            binding.dateTv.text = resources.getString(EventString.date, item.date)
        }
    }

    inner class HeaderViewHolder(private val headerBinding: HeaderLayoutBinding) :
        RecyclerView.ViewHolder(headerBinding.root) {

        fun bind(header: SearchEventState.SearchHeader) {
            headerBinding.headerTitle.text = header.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return if (viewType == ITEM_LAYOUT)
            ViewHolder(SearchItemLayoutBinding.inflate(layoutInflater, parent, false))
        else HeaderViewHolder(HeaderLayoutBinding.inflate(layoutInflater, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItem(position) is SearchItem)
            (holder as ViewHolder).bind(getItem(position) as SearchItem)
        else (holder as HeaderViewHolder).bind(getItem(position) as SearchEventState.SearchHeader)
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is SearchItem -> ITEM_LAYOUT
            else -> HEADER_LAYOUT
        }
    }
}
