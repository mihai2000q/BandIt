package com.bandit.constant

sealed class BandItEnums {
    object Home {
        enum class NavigationType { Bottom, Drawer }
    }
    object Concert {
        enum class Type(i: Int) {
            Tournament(1),
            Simple(2),
            Festival(3)
        }
    }
}
