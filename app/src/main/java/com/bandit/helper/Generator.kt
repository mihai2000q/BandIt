package com.bandit.helper

import kotlin.random.Random

sealed class Generator {
    companion object {
        fun generateRandomConcertId(): Int {
            return Random.nextInt(Constants.INT_MAX)
        }
    }
}
