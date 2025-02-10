package com.example.foodify.bookmarks.view

sealed class SyncState {
    data class Success(val message: String) : SyncState()
    data class Error(val message: String) : SyncState()
    data class NoChange(val message: String) : SyncState()
    data class NoBackup(val message: String) : SyncState()
    data object Loading : SyncState()
}
