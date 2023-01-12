package com.bandit.builder.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.bandit.R
import com.bandit.data.model.Concert
import com.bandit.databinding.ModelConcertBinding
import com.bandit.constant.BandItEnums
import com.bandit.helper.Normalization
import java.time.LocalDateTime

class ConcertAdapter(
    private val concerts: List<Concert>,
    private val onConcertClick: (Concert) -> Unit,
    private val onConcertLongClick: (Concert) -> Boolean,
    private val onDeleteItem: (Concert) -> Boolean,
    private val onEditItem: (Concert) -> Boolean
) : RecyclerView.Adapter<ConcertAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ModelConcertBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ModelConcertBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val concert = concerts[position]

        holder.itemView.setOnClickListener { onConcertClick(concert) }
        holder.itemView.setOnLongClickListener {
            val popupMenu = PopupMenu(holder.binding.root.context, holder.itemView)
            popupMenu.inflate(R.menu.item_popup_menu)
            popupMenu.setOnMenuItemClickListener {
                popupMenu.dismiss()
                when(it.itemId) {
                    R.id.popup_menu_delete -> {
                        onDeleteItem(concert)
                    }
                    else -> {
                        onEditItem(concert)
                    }
                }
            }
            popupMenu.show()
            onConcertLongClick(concert)
        }

        with(holder.binding) {
            when(concert.type) {
                BandItEnums.Concert.Type.Simple -> concertLayout.setBackgroundColor(Color.CYAN)
                BandItEnums.Concert.Type.Tournament -> concertLayout.setBackgroundColor(Color.RED)
                BandItEnums.Concert.Type.Festival -> concertLayout.setBackgroundColor(Color.GREEN)
            }
            concertTitle.text = concert.name.uppercase()
            concertCityCountry.text = "${Normalization.normalizeWord(concert.city)}, " +
                    Normalization.normalizeWord(concert.country)
            concertPlace.text = concert.place
            concertDate.text = when {
                isConcert7DaysApart(concert) -> Normalization.normalizeWord(concert.dateTime.dayOfWeek.name)
                isConcertOneYearApart(concert) -> "${concert.dateTime.dayOfMonth} " +
                        Normalization.normalizeWord(concert.dateTime.month.name.substring(0..2)) +
                        " ${concert.dateTime.year}"
                else -> "${concert.dateTime.dayOfMonth} " +
                        Normalization.normalizeWord(concert.dateTime.month.name)
            }
        }
    }

    override fun getItemCount(): Int {
        return concerts.size
    }

    private fun isConcert7DaysApart(concert: Concert): Boolean {
        return  LocalDateTime.now().year == concert.dateTime.year &&
                LocalDateTime.now().month == concert.dateTime.month &&
                concert.dateTime.dayOfMonth - LocalDateTime.now().dayOfMonth <= 7
    }

    private fun isConcertOneYearApart(concert: Concert): Boolean {
        return concert.dateTime.year - LocalDateTime.now().year > 0
    }
}