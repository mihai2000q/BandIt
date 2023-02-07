package com.bandit.data.model

import com.bandit.util.AndroidUtils
import java.time.LocalDateTime

data class Note(
    val title: String,
    val content: String,
    val createdOn: LocalDateTime = LocalDateTime.now(),
    val accountId: Long,
    override val id: Long = AndroidUtils.generateRandomLong()
) : BaseModel(id)