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
import com.bandit.extension.StringExtensions.get2Characters
import com.bandit.extension.StringExtensions.normalizeWord

data class ConcertAdapter(
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
                popupMenu(holder, concert)
                onConcertLongClick(concert)
            }

            with(binding) {
                val color: Int = if(concert.isOutdated())
                    Color.RED
                else
                    when(concert.type) {
                        BandItEnums.Concert.Type.Simple -> Color.CYAN
                        BandItEnums.Concert.Type.Tournament -> Color.GRAY
                        BandItEnums.Concert.Type.Festival -> Color.GREEN
                    }
                concertLayout.setBackgroundColor(color)
                concertTitle.text = concert.name.uppercase()
                concertCityCountry.text = "${concert.city.normalizeWord()}, " +
                        concert.country.normalizeWord()
                concertPlace.text = concert.place
                concertDate.text = when {
                    concert.is24HoursApart() -> "${concert.dateTime.hour.toString().get2Characters()}:" +
                            concert.dateTime.minute.toString().get2Characters()
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

    private fun popupMenu(holder: ViewHolder, concert: Concert) {
        val popupMenu = PopupMenu(holder.binding.root.context, holder.itemView)
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
    }

    override fun getItemCount(): Int {
        return concerts.size
    }
}