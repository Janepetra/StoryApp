package com.storyapp.dashboard

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.storyapp.adapter.ListStoryAdapter
import com.storyapp.viewmodel.DetailStoryViewModel
import com.storyapp.viewmodel.SettingModelFactory
import com.storyapp.viewmodel.SettingPreferences
import com.storyapp.viewmodel.SettingViewModel
import com.storyapp.viewmodel.ViewModelFactory
import com.storyapp.viewmodel.dataStore
import com.storyapp.databinding.FragmentHomeBinding
import java.util.Timer
import kotlin.concurrent.schedule

class HomeFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    private lateinit var binding: FragmentHomeBinding
    private var storyViewModel: DetailStoryViewModel? = null
    private var rvListStory = ListStoryAdapter()
    private var token = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.swipeRefresh.setOnRefreshListener {
            onRefresh()
        }

        //get token from preferences
        storyViewModel = ViewModelProvider(this, ViewModelFactory(activity as MainActivity))[DetailStoryViewModel::class.java]
        val pref = SettingPreferences.getInstance((activity as MainActivity).dataStore)
        val settingViewModel = ViewModelProvider(this, SettingModelFactory(pref))[SettingViewModel::class.java]

        settingViewModel.getUserTokens().observe(viewLifecycleOwner) {
            token = StringBuilder("Bearer ").append(it).toString()
            storyViewModel?.getListStory(token)
        }

        setListUser()
        showLoading()

        return binding.root
    }

    override fun onRefresh() {
        binding.swipeRefresh.isRefreshing = true
        storyViewModel?.getListStory(token)
        Timer().schedule(2000) {
            binding.swipeRefresh.isRefreshing = false
        }
        binding.nestedScrollView.smoothScrollTo(0, 0)
    }

    //set recyler view
    private fun setListUser() {
        binding.rvStory.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            isNestedScrollingEnabled = false
            adapter = rvListStory
        }
        storyViewModel?.listStory?.observe(viewLifecycleOwner) {
            rvListStory.apply {
                initData(it)
                notifyDataSetChanged()
            }
        }
    }

    private fun showLoading() {
        storyViewModel?.isLoading?.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }
}