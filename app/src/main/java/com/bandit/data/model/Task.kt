package com.bandit.data.model

import com.bandit.util.AndroidUtils
import java.time.LocalDateTime

data class Task(
    val checked: Boolean,
    val message: String,
    val bandId: Long,
    val createdOn: LocalDateTime = LocalDateTime.now(),
    override val id: Long = AndroidUtils.generateRandomLong()
) : BaseModel(id)
