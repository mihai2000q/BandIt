package com.bandit.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bandit.data.model.Account
import com.bandit.databinding.ModelFriendBinding
import com.bandit.extension.normalizeWord

class PeopleAdapter(
    private val accounts: List<Account>,
    private val onClick: ((Account) -> Unit)? = null
) : RecyclerView.Adapter<PeopleAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ModelFriendBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ModelFriendBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return accounts.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val account = accounts[position]

        with(holder) {
            itemView.setOnClickListener { onClick?.invoke(account) }
            with(binding) {
                friendName.text = account.name
                friendNickname.text = account.nickname
                friendRole.text = account.role.name.normalizeWord()
                friendBand.text = ""
            }
        }
    }

}