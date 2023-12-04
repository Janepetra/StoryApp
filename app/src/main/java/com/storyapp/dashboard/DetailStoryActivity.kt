package com.storyapp.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.storyapp.customview.Helper
import com.storyapp.databinding.ActivityDetailStoryBinding
import com.storyapp.viewmodel.DetailStoryViewModel

class DetailStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailStoryBinding
    private val storyViewModel: DetailStoryViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setUI()
        getData()
        showLoading()
    }

    private fun setUI() {
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    //get data use helper
    private fun getData() {
        binding.tvDetName.text = intent.getData(Helper.data.Name.name,"")
        Glide.with(binding.root)
            .load(intent.getData(Helper.data.ImageURL.name, ""))
            .into(binding.ivDetPhoto)
        binding.tvDetDesc.text =
            intent.getData(Helper.data.Description.name, "")
    }
    // Mendapatkan data dari Intent berdasarkan key
    private fun Intent.getData(key: String, defaultValue: String = "None"): String {
        return getStringExtra(key) ?: defaultValue
    }

    private fun showLoading() {
        storyViewModel?.isLoading?.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE

        }
    }
}