package com.bandit.data.model

import com.bandit.constant.BandItEnums
import com.bandit.util.AndroidUtils
import java.time.LocalDateTime

open class Event(
    open val name: String,
    open val dateTime: LocalDateTime,
    val type: BandItEnums.Event.Type,
    open val bandId: Long,
    override val id: Long = AndroidUtils.generateRandomLong()
) : BaseModel(id)