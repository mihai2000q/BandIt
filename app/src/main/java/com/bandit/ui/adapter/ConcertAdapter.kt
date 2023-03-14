package com.bandit.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bandit.R
import com.bandit.constant.BandItEnums
import com.bandit.data.model.Concert
import com.bandit.databinding.ModelConcertBinding
import com.bandit.extension.print
import com.bandit.ui.concerts.ConcertDetailDialogFragment
import com.bandit.ui.concerts.ConcertsViewModel
import com.bandit.util.AndroidUtils

data class ConcertAdapter(
    private val fragment: Fragment,
    private val concerts: List<Concert>,
    private val viewModel: ConcertsViewModel,
    private val onDeleteConcert: (Concert) -> Unit,
    private val onEditConcert: (Concert) -> Unit
) : RecyclerView.Adapter<ConcertAdapter.ViewHolder>() {
    private val concertDetailDialogFragment = ConcertDetailDialogFragment()
    private lateinit var popupMenu: PopupMenu
    private var isPopupShown = false

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
                val color = if(concert.isOutdated())
                    R.color.faded_red
                else
                    when(concert.concertType) {
                        BandItEnums.Concert.Type.Simple -> R.color.faded_green
                        BandItEnums.Concert.Type.Tournament -> R.color.faded_blue_to_green
                        BandItEnums.Concert.Type.Festival -> R.color.faded_purple_to_blue
                    }
                concertCard.setCardBackgroundColor(ContextCompat.getColor(
                    holder.binding.root.context, color))
                concertTitle.text = concert.name
                concertDate.text = concert.printExplicitDate()
                concertTime.text = concert.dateTime.toLocalTime().print()
                AndroidUtils.ifNullHide(concertCity, concert.city)
                AndroidUtils.ifNullHide(concertCountry, concert.country)
                AndroidUtils.ifNullHide(concertPlace, concert.place)
            }
        }
    }

    private fun popupMenu(holder: ViewHolder, concert: Concert) {
        popupMenu = PopupMenu(holder.binding.root.context, holder.itemView)
        popupMenu.inflate(R.menu.item_popup_menu)
        popupMenu.setOnDismissListener { isPopupShown = false }
        popupMenu.setOnMenuItemClickListener {
            popupMenu.dismiss()
            when (it.itemId) {
                R.id.popup_menu_delete -> onDelete(concert)
                else -> onEdit(concert)
            }
        }
    }

    override fun getItemCount(): Int {
        return concerts.size
    }

    private fun onClick(concert: Concert) {
        viewModel.selectedConcert.value = concert
        AndroidUtils.showDialogFragment(
            concertDetailDialogFragment,
            fragment.childFragmentManager
        )
    }

    private fun onLongClick(holder: ConcertAdapter.ViewHolder, concert: Concert): Boolean {
        popupMenu(holder, concert)
        if(!isPopupShown) {
            isPopupShown = true
            popupMenu.show()
        }
        return true
    }

    private fun onDelete(concert: Concert): Boolean {
        onDeleteConcert(concert)
        return true
    }

    private fun onEdit(concert: Concert): Boolean {
        onEditConcert(concert)
        return true
    }
}