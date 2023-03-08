package com.bandit.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bandit.R
import com.bandit.constant.BandItEnums
import com.bandit.data.model.Concert
import com.bandit.databinding.ModelConcertBinding
import com.bandit.extension.normalizeWord
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
                val color: Int = if(concert.isOutdated())
                    Color.RED
                else
                    when(concert.concertType) {
                        BandItEnums.Concert.Type.Simple -> Color.CYAN
                        BandItEnums.Concert.Type.Tournament -> Color.GRAY
                        BandItEnums.Concert.Type.Festival -> Color.GREEN
                        else -> Color.LTGRAY
                    }
                concertLayout.setBackgroundColor(color)
                concertTitle.text = concert.name
                if(!concert.city.isNullOrEmpty() || !concert.country.isNullOrEmpty())
                    concertCityCountry.text = buildString {
                        append("${concert.city?.normalizeWord()}, ")
                        append(concert.country?.normalizeWord())
                    }
                else
                    concertCityCountry.visibility = View.GONE
                AndroidUtils.ifNullHide(concertPlace, concert.place)
                concertDate.text = concert.printExplicitDateTime()
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