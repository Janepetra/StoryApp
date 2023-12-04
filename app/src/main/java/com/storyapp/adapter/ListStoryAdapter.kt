package com.storyapp.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.storyapp.customview.Helper
import com.storyapp.dashboard.DetailStoryActivity
import com.storyapp.databinding.ItemRowBinding
import com.storyapp.response.Story

class ListStoryAdapter: RecyclerView.Adapter<ListStoryAdapter.MyViewHolder>() {
    private var story = mutableListOf<Story>()

    override fun onCreateViewHolder( parent: ViewGroup, viewType: Int ): MyViewHolder {
        val binding = ItemRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int = story.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) = holder.bind(story[position])

    fun initData(stories: List<Story>) {
        story.clear()
        story = stories.toMutableList()
    }

    inner class MyViewHolder(private val binding: ItemRowBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(storyList: Story) {
            Log.d("ListStoryAdapter", "error while parsing data")
            with(binding) {
                Glide.with(this.root.context)
                    .load(storyList.photoUrl)
                    .into(binding.ivProfile)
                tvName.text = storyList.name
                upTime.text = storyList.createdAt
            }
            itemView.setOnClickListener {
                //send data to detail use helper
                val intentDetail = Intent(itemView.context, DetailStoryActivity::class.java)
                intentDetail.putExtra(Helper.data.Name.name, storyList.name)
                intentDetail.putExtra(Helper.data.ImageURL.name, storyList.photoUrl)
                intentDetail.putExtra(Helper.data.Description.name, storyList.description)
                intentDetail.putExtra(Helper.data.UploadTime.name, storyList.createdAt)
                itemView.context.startActivity(intentDetail)
            }
        }
    }
}