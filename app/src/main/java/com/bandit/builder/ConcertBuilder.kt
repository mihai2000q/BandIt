package com.bandit.builder

import android.content.Context
import android.widget.TableRow
import com.bandit.data.model.Concert

interface ConcertBuilder {
    fun buildConcertTableRow(context: Context, concert: Concert): TableRow
}