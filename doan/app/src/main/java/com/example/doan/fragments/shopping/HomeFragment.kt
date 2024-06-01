package com.example.doan.fragments.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.doan.R
import com.example.doan.adapter.HomeViewpagerAdapter
import com.example.doan.databinding.FragmentHomeBinding
import com.example.doan.fragments.categories.AccessoryFragment
import com.example.doan.fragments.categories.ChairFragment
import com.example.doan.fragments.categories.CupboardFragment
import com.example.doan.fragments.categories.FurnitureFragment
import com.example.doan.fragments.categories.MainCategoryFragment
import com.example.doan.fragments.categories.TableFragment
import com.google.android.material.tabs.TabLayoutMediator

class HomeFragment : Fragment(R.layout.fragment_home) {
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val categoriesFragments = arrayListOf(
            MainCategoryFragment(),
            ChairFragment(),
            CupboardFragment(),
            TableFragment(),
            AccessoryFragment(),
            FurnitureFragment()
        )

        binding.viewpagerHome.isUserInputEnabled = false

        val viewPager2Adapter =
            HomeViewpagerAdapter(categoriesFragments, childFragmentManager, lifecycle)
        binding.viewpagerHome.adapter = viewPager2Adapter
        TabLayoutMediator(binding.tabLayout, binding.viewpagerHome) { tab, position ->
            when (position) {
                0 -> tab.text = "Main"
                1 -> tab.text = "Meats"
                2 -> tab.text = "Milks"
                3 -> tab.text = "Vegetables"
                4 -> tab.text = "Cakes"
                5 -> tab.text = "Wines"
            }
        }.attach()
    }
}