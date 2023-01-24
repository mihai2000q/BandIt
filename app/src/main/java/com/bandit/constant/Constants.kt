package com.bandit.constant

object Constants {
    const val MAX_NR_ITEMS = 100_000L
    object Login {
        const val VIEW_MODEL_TAG = "LoginViewModel"
    }
    object Account {
        const val TAG = "ACCOUNT DIALOG FRAGMENT"
    }
    object Band {
        const val CREATE_BAND_TAG = "CREATE BAND DIALOG FRAGMENT"
        const val BAND_TAG = "BAND DIALOG FRAGMENT"
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
            const val userUid = "userUid"
        }
        const val ADD_CONCERT_TAG = "ADD CONCERT DIALOG FRAGMENT"
        const val DETAIL_CONCERT_TAG = "DETAIL CONCERT DIALOG FRAGMENT"
        const val EDIT_CONCERT_TAG = "EDIT CONCERT DIALOG FRAGMENT"
        const val FILTER_CONCERT_TAG = "FILTER CONCERT DIALOG FRAGMENT"
    }
    object Firebase {
        object Database {
            const val USER_ACCOUNT_SETUPS = "UserAccountSetups"
            const val ACCOUNTS = "Accounts"
            const val BANDS = "Bands"
            const val BAND_INVITATIONS = "BandInvitation"
            const val CONCERTS = "Concerts"
            const val TAG = "FirebaseDB"
        }
        const val AUTH_TAG = "FirebaseAuth"
    }
    object Preferences {
        const val APP_PREFERENCES = "App Preferences"
        const val REMEMBER_ME = "Remember Me "
    }
}
