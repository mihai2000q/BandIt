package com.bandit.constant

object BandItEnums {
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
            Week,
            Day
        }
    }
    object Event {
        enum class Type {
            Simple,
            Training,
            Concert,
            Composing
        }
    }
}
