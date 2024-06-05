package com.example.doan.adapter

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
// một adapter tùy chỉnh cho ViewPager2 trong Android
//Lớp HomeViewpagerAdapter này được sử dụng để kết nối một danh sách các fragment với một ViewPager2
class HomeViewpagerAdapter(
    private val fragments: List<Fragment>,
    fm: FragmentManager,
    lifecycle: Lifecycle
):FragmentStateAdapter(fm,lifecycle) {

    override fun getItemCount(): Int {
        return fragments.size
    }
// Phương thức này tạo ra một fragment cho vị trí được chỉ định (position). Nó trả về fragment tương ứng từ danh sách
    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}