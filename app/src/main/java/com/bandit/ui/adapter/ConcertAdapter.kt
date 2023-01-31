package com.bandit.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.bandit.R
import com.bandit.data.model.Concert
import com.bandit.databinding.ModelConcertBinding
import com.bandit.constant.BandItEnums
import com.bandit.extension.get2Characters
import com.bandit.extension.normalizeWord
import com.bandit.ui.concerts.ConcertDetailDialogFragment
import com.bandit.ui.concerts.ConcertEditDialogFragment
import com.bandit.ui.concerts.ConcertsViewModel
import com.bandit.util.AndroidUtils

data class ConcertAdapter(
    private val concerts: List<Concert>,
    private val viewModel: ConcertsViewModel,
    private val childFragmentManager: FragmentManager
) : RecyclerView.Adapter<ConcertAdapter.ViewHolder>() {
    private val concertEditDialogFragment = ConcertEditDialogFragment()
    private val concertDetailDialogFragment = ConcertDetailDialogFragment()

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

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val concert = concerts[position]

        with(holder) {
            itemView.setOnClickListener { onClick(concert) }
            itemView.setOnLongClickListener { onLongClick(holder, concert) }

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
                concertCityCountry.text = buildString {
                    append("${concert.city.normalizeWord()}, ")
                    append(concert.country.normalizeWord())
                }
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
                R.id.popup_menu_delete -> onDelete(holder, concert)
                else -> onEdit(concert)
            }
        }
        popupMenu.show()
    }

    override fun getItemCount(): Int {
        return concerts.size
    }

    private fun onClick(concert: Concert) {
        viewModel.selectedConcert.value = concert
        AndroidUtils.showDialogFragment(
            concertDetailDialogFragment,
            childFragmentManager
        )
    }

    private fun onLongClick(holder: ConcertAdapter.ViewHolder, concert: Concert): Boolean {
        popupMenu(holder, concert)
        viewModel.selectedConcert.value = concert
        return true
    }

    private fun onDelete(holder: ConcertAdapter.ViewHolder, concert: Concert): Boolean {
        AndroidUtils.toastNotification(
            holder.binding.root.context,
            holder.binding.root.resources.getString(R.string.concert_remove_toast),
        )
        return viewModel.removeConcert(concert)
    }

    private fun onEdit(concert: Concert): Boolean {
        viewModel.selectedConcert.value = concert
        AndroidUtils.showDialogFragment(
            concertEditDialogFragment,
            childFragmentManager
        )
        return true
    }
}