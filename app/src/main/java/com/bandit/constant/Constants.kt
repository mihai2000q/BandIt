package com.bandit.constant

object Constants {
    const val MAX_NR_ITEMS = 100_000
    object Home {
        const val ACCOUNT_HOME_TAG = "ACCOUNT HOME DIALOG FRAGMENT"
    }
    object Concert {
        object Fields {
            const val id = "id"
            const val name = "name"
            const val dateTime = "dateTime"
            const val city = "city"
            const val country = "country"
            const val place = "place"
            const val type = "type"
        }
        const val ADD_CONCERT_TAG = "ADD CONCERT DIALOG FRAGMENT"
        const val DETAIL_CONCERT_TAG = "DETAIL CONCERT DIALOG FRAGMENT"
        const val EDIT_CONCERT_TAG = "EDIT CONCERT DIALOG FRAGMENT"
        const val FILTER_CONCERT_TAG = "FILTER CONCERT DIALOG FRAGMENT"
    }
    object Firebase {
        const val DATABASE_TAG = "FirebaseDB"
        const val AUTH_TAG = "FirebaseAuth"
    }
    object Preferences {
        const val APP_PREFERENCES = "App Preferences"
        const val REMEMBER_ME = "Remember Me "
    }
}
