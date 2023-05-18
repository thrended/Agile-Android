package com.example.agileandroidalpha.feature_board.presentation.search

import com.example.agileandroidalpha.firebase.firestore.model.FireUser
import com.example.agileandroidalpha.firebase.firestore.model.Sprint
import com.example.agileandroidalpha.firebase.firestore.model.Story
import com.example.agileandroidalpha.firebase.firestore.model.SubTask

data class SearchState(
    val searchQuery: String = "",
    val searchHistory: List<String> = emptyList(),
    val searchText: String = "",
    val isSearching: Boolean = false,
    val mode: String = "None",
    val users: List<FireUser> = emptyList(),
    val sprints: List<Sprint> = emptyList(),
    val stories: List<Story> = emptyList(),
    val subtasks: List<SubTask> = emptyList()
)
