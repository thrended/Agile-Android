package com.example.agileandroidalpha.feature_board.presentation.search

sealed class SearchEvent {
    data class ChangeSearchText(val string: String): SearchEvent()
    data class ChangeMode(val mode: String): SearchEvent()
}
