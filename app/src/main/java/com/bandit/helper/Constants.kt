package com.bandit.helper

sealed class Constants {
    enum class NavigationType { Bottom, Drawer }
    companion object {
        const val INT_MAX = 100_000
    }
}
