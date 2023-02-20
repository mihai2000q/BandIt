package com.bandit.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bandit.constant.BandItEnums
import com.bandit.databinding.ModelHomeButtonBinding
import com.bandit.util.AndroidUtils
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeButtonAdapter(
    private val fragment: Fragment,
    private val homeButtons: Map<String, BandItEnums.Home.NavigationType>,
    private val bottomNavigationBar: BottomNavigationView
) : RecyclerView.Adapter<HomeButtonAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ModelHomeButtonBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ModelHomeButtonBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return homeButtons.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val key = homeButtons.keys.toList()[position]

        with(holder.binding.homeNavigationButton) {
            this.width = AndroidUtils.getScreenWidth(fragment.requireActivity()) * 7 / 16
            this.height = AndroidUtils.getScreenHeight(fragment.requireActivity()) / 5
            text = buildString {
                append("Your ")
                append(key)
            }
            val destination: Int = com.bandit.R.id::class.java.fields.find {
                it.name.equals("navigation_" +
                        key.lowercase().replace("\\s".toRegex(), "")
                )
            }?.getInt(null) ?: com.bandit.R.id.navigation_home
            when(homeButtons[key]) {
                BandItEnums.Home.NavigationType.Bottom -> this.setOnClickListener {
                    bottomNavigationBar.selectedItemId = destination
                }
                BandItEnums.Home.NavigationType.Drawer -> this.setOnClickListener {
                    //TODO: Find a way to either make this work or the navController
                    //drawerNav.setCheckedItem(destination)
                }
                else -> {}
            }
        }
    }

}