package com.bandit.ui.adapter

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
import com.bandit.extension.StringExtensions.normalizeWord

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

        with(holder) {
            itemView.setOnClickListener { onConcertClick(concert) }
            itemView.setOnLongClickListener {
                val popupMenu = PopupMenu(binding.root.context, itemView)
                popupMenu.inflate(R.menu.item_popup_menu)
                popupMenu.setOnMenuItemClickListener {
                    popupMenu.dismiss()
                    when (it.itemId) {
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

            with(binding) {
                when (concert.type) {
                    BandItEnums.Concert.Type.Simple -> concertLayout.setBackgroundColor(Color.CYAN)
                    BandItEnums.Concert.Type.Tournament -> concertLayout.setBackgroundColor(Color.RED)
                    BandItEnums.Concert.Type.Festival -> concertLayout.setBackgroundColor(Color.GREEN)
                }
                concertTitle.text = concert.name.uppercase()
                concertCityCountry.text = "${concert.city.normalizeWord()}, " +
                        concert.country.normalizeWord()
                concertPlace.text = concert.place
                concertDate.text = when {
                    concert.is7DaysApart() -> concert.dateTime.dayOfWeek.name.normalizeWord()
                    concert.isOneYearApart() -> "${concert.dateTime.dayOfMonth} " +
                            concert.dateTime.month.name.substring(0..2).normalizeWord() +
                            " ${concert.dateTime.year}"
                    else -> "${concert.dateTime.dayOfMonth} " +
                            concert.dateTime.month.name.normalizeWord()
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return concerts.size
    }
}