package com.bandit.ui.helper

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.bandit.R
import com.bandit.data.template.Item

open class TouchHelper<T: Item>(
    private val context: Context,
    private val recyclerView: RecyclerView,
    private val onLeftAction: (T) -> Unit,
    private val onRightAction: (T) -> Unit,
    private val drawableIdRightAction: Int = R.drawable.ic_edit
) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
    // has to extend this class to populate this list
    lateinit var items: List<T>
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean { TODO("Not yet implemented") }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition
        if(direction == ItemTouchHelper.LEFT) {
            onLeftAction(items[position])
            recyclerView.adapter?.notifyItemChanged(position)
        }
        else if(direction == ItemTouchHelper.RIGHT) {
            onRightAction(items[position])
            recyclerView.adapter?.notifyItemChanged(position)
        }
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

        val icon: Drawable
        val background: ColorDrawable

        val itemView = viewHolder.itemView
        val offset = 20
        if(dX > 0) {
            icon = ContextCompat.getDrawable(
                context,
                drawableIdRightAction
            )!!
            background = ColorDrawable(ContextCompat.getColor(
                context,
                R.color.dark_spring_green
            ))
        }
        else {
            icon = ContextCompat.getDrawable(
                context,
                R.drawable.ic_delete
            )!!
            background = ColorDrawable(Color.RED)
        }

        val iconMargin = (itemView.height - icon.intrinsicHeight) / 2
        val iconTop = itemView.top + iconMargin
        val iconBottom = iconTop + icon.intrinsicHeight

        if(dX > 0) {
            val iconLeft = itemView.left + iconMargin
            val iconRight = iconLeft + icon.intrinsicWidth
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
            background.setBounds(itemView.left, itemView.top,
                (itemView.left + dX + offset).toInt(), itemView.bottom)
        } else if (dX < 0) {
            val iconRight = itemView.right - iconMargin
            val iconLeft = iconRight - icon.intrinsicWidth
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
            background.setBounds((itemView.right + dX - offset).toInt(), itemView.top,
                itemView.right, itemView.bottom)
        }
        else
            background.setBounds(0,0,0,0)
        background.draw(c)
        icon.draw(c)
    }
}
