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
    object Account {
        enum class Role {
            LeadGuitar,
            RhythmGuitar,
            Bass,
            Vocalist,
            Drums,
            Manager
        }
    }
    object Schedule {
        enum class ViewType {
            Month,
            Week
        }
    }
    object Event {
        enum class Type {
            Simple,
            Training,
            Concert
        }
    }
}
