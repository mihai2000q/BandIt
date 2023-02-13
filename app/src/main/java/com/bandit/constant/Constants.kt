package com.bandit.constant

object Constants {
    const val MAX_NR_ITEMS = 100_000L
    const val SPLASH_SCREEN_DELAY = 3_000L //ms
    const val PASSWORD_MIN_CHARACTERS = 8
    const val TIMEOUT_INTERNET_CONNECTION_TEST = 10_000L //ms
    object SafeArgs {
        const val FAIL_LOGIN_NETWORK = "Fail Login Due To Network Connection Missing"
        const val REMEMBER_ME = "Remember Me"
    }
    object Firebase {
        object Database {
            const val USER_ACCOUNT_SETUPS = "UserAccountSetups"
            const val INTERNET_CONNECTION_COLLECTION = "InternetConnection"
            const val INTERNET_CONNECTION_DOCUMENT = "ICTest"
            const val ACCOUNTS = "Accounts"
            const val BANDS = "Bands"
            const val BAND_INVITATIONS = "BandInvitations"
            const val CONCERTS = "Concerts"
            const val SONGS = "Songs"
            const val ALBUMS = "Albums"
            const val EVENTS = "Events"
            const val TASKS = "Tasks"
            const val NOTES = "Notes"
            const val TAG = "FirebaseDB"
        }
        object Auth {
            const val TAG = "FirebaseAuth"
        }
    }
    object Preferences {
        const val APP_PREFERENCES = "App Preferences"
        const val REMEMBER_ME = "Remember Me"
    }
    object Login {
        const val VIEW_MODEL_TAG = "LoginViewModel"
    }
    object Signup {
        const val VIEW_MODEL_TAG = "SignUpViewModel"
    }
    object FirstLogin {
        const val VIEW_MODEL_TAG = "FirstLoginViewModel"
    }
    object Account {
        const val TAG = "ACCOUNT DIALOG FRAGMENT"
        const val VIEW_MODEL_TAG = "AccountViewModel"
    }
    object Band {
        const val CREATE_BAND_TAG = "CREATE BAND DIALOG FRAGMENT"
        const val BAND_TAG = "BAND DIALOG FRAGMENT"
        const val VIEW_MODEL_TAG = "BandViewModel"
    }
    object Home {
        const val BAND_INVITATION_TAG = "BAND INVITATION DIALOG FRAGMENT"
        const val VIEW_MODEL_TAG = "HomeViewModel"
    }
    object Concert {
        const val ADD_TAG = "ADD CONCERT DIALOG FRAGMENT"
        const val DETAIL_TAG = "DETAIL CONCERT DIALOG FRAGMENT"
        const val EDIT_TAG = "EDIT CONCERT DIALOG FRAGMENT"
        const val FILTER_TAG = "FILTER CONCERT DIALOG FRAGMENT"
        const val VIEW_MODEL_TAG = "ConcertsViewModel"
    }
    object Song {
        object Album {
            const val ADD_TAG = "ADD ALBUM DIALOG FRAGMENT"
            const val ADD_SONG_TAG = "ADD SONG TO ALBUM DIALOG FRAGMENT"
            const val DETAIL_TAG = "DETAIL ALBUM DIALOG FRAGMENT"
            const val EDIT_TAG = "EDIT ALBUM DIALOG FRAGMENT"
            const val FILTER_TAG = "FILTER ALBUM DIALOG FRAGMENT"
        }
        const val ADD_TAG = "ADD SONG DIALOG FRAGMENT"
        const val DETAIL_TAG = "DETAIL SONG DIALOG FRAGMENT"
        const val EDIT_TAG = "EDIT SONG DIALOG FRAGMENT"
        const val FILTER_TAG = "FILTER SONG DIALOG FRAGMENT"
        const val VIEW_MODEL_TAG = "SongsViewModel"
    }
    object Social {
        const val NUMBER_OF_TABS = 2
        object Chats {
            const val VIEW_MODEL_TAG = "ChatsViewModel"
        }
        object Friends {
            const val VIEW_MODEL_TAG = "FriendsViewModel"
        }
    }
    object Schedule {
        const val ADD_TAG = "ADD SCHEDULE DIALOG FRAGMENT"
        const val DETAIL_TAG = "DETAIL SCHEDULE DIALOG FRAGMENT"
        const val EDIT_TAG = "EDIT SCHEDULE DIALOG FRAGMENT"
        const val VIEW_MODEL_TAG = "ScheduleViewModel"
    }
    object ToDoList {
        const val ADD_TAG = "ADD TO DO LIST BOTTOM SHEET DIALOG FRAGMENT"
        const val VIEW_MODEL_TAG = "ToDoListViewModel"
    }
    object PersonalNotes {
        const val EDIT_TAG = "EDIT PERSONAL NOTE DIALOG FRAGMENT"
        const val VIEW_MODEL_TAG = "PersonalNotesViewModel"
    }
}
