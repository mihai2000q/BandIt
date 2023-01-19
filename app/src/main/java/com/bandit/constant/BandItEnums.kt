package com.bandit.constant

sealed class BandItEnums {
    object Home {
        enum class NavigationType { Bottom, Drawer }
    }
    object Concert {
        enum class Type {
            Simple,
            Tournament,
            Festival
        }
    }
}
