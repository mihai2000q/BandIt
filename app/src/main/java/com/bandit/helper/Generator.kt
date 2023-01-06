package com.bandit.helper

import kotlin.random.Random

object Generator {
    fun generateRandomConcertId() = Random.nextInt(Constants.INT_MAX)
}
