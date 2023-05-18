package com.example.agileandroidalpha.feature_board.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agileandroidalpha.firebase.firestore.Statics
import com.example.agileandroidalpha.firebase.firestore.model.ChatMessage
import com.example.agileandroidalpha.firebase.firestore.model.FireUser
import com.example.agileandroidalpha.firebase.firestore.model.Sprint
import com.example.agileandroidalpha.firebase.firestore.model.Story
import com.example.agileandroidalpha.firebase.firestore.model.SubTask
import com.example.agileandroidalpha.firebase.repository.FirestoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@FlowPreview
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repo: FirestoreRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SearchState())
    val state = _state.asStateFlow()

    private val _text = MutableStateFlow("")
    val text = _text.asStateFlow()

    private val _mode = MutableStateFlow("All")
    val mode = _mode.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    private val _currentUser = MutableStateFlow(FireUser())
    val currentUser = _currentUser.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(
            5000L,
            //500L,
        ),
        _currentUser.value
    )

    private val _chats = MutableStateFlow(Statics.chatLog as List<ChatMessage>)
    val chats = text
        .debounce(750L)
        .onEach { _isSearching.update { true } }
        .combine(_chats) { txt, messages ->
            if(txt.isBlank()) {
                messages
            } else {
                delay(500L)
                messages.filter {
                    it.matchesSearchQuery(txt)
                }.distinctBy { u -> u.id }
            }
        }
        .onEach { _isSearching.update { false } }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(
                5000L,
                //500L,
            ),
            _chats.value
        )

    private val _users = MutableStateFlow(Statics.usersList as List<FireUser>)
    val users = text
        .debounce(750L)
        .onEach { _isSearching.update { true } }
        .combine(_users) { txt, users ->
            if(txt.isBlank()) {
                users
            } else {
                delay(500L)
                users.filter {
                    it.matchesSearchQuery(txt)
                }.distinctBy { u -> u.id }
            }
        }
        .onEach { _isSearching.update { false } }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(
                5000L,
                //500L,
            ),
            _users.value
        )
//    val users = _users.asStateFlow()

    private val _sprints = MutableStateFlow(Statics.sprintsList as List<Sprint>)
    val sprints = text
        .debounce(750L)
        .onEach { _isSearching.update { true } }
        .combine(_sprints) { txt, sps ->
            if(txt.isBlank()) {
                sps
            } else {
                delay(500L)
                sps.filter {
                    it.matchesSearchQuery(txt)
                }.distinctBy { u -> u.id }
            }
        }
        .onEach { _isSearching.update { false } }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(
                5000L,
                //500L,
            ),
            _sprints.value
        )
//    val sprints = _sprints.asStateFlow()

    private val _stories = MutableStateFlow(Statics.storiesList as List<Story>)
    @OptIn(FlowPreview::class)
    val stories = text
        .debounce(750L)
        .onEach { _isSearching.update { true } }
        .combine(_stories) { txt, sts ->
            if(txt.isBlank()) {
                sts
            } else {
                delay(500L)
                sts.filter {
                    it.matchesSearchQuery(txt)
                }.distinctBy { u -> u.id }
            }
        }
        .onEach { _isSearching.update { false } }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(
                5000L,
                //500L,
            ),
            _stories.value
        )
//    val stories = _stories.asStateFlow()

    private val _subtasks = MutableStateFlow(Statics.subtasksList as List<SubTask>)
    @OptIn(FlowPreview::class)
    val subtasks = text
        .debounce(750L)
        .onEach { _isSearching.update { true } }
        .combine(_subtasks) { txt, sbs ->
            if(txt.isBlank()) {
                sbs
            } else {
                delay(500L)
                sbs.filter {
                    it.matchesSearchQuery(txt)
                }.distinctBy { u -> u.id }
            }
        }
        .onEach { _isSearching.update { false } }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(
                5000L,
                //500L,
            ),
            _subtasks.value
        )
//    val subtasks = _subtasks.asStateFlow()


    init {
        viewModelScope.launch {
            repo.getAllChats { ls, map ->

            }
            repo.fetchFirestoreChanges(){ sps, fireUsers, loggedInUser, stores, subtasks, map, allT, bin  ->
                viewModelScope.launch {

                }
            }
        }
    }

    fun onEvent(event: SearchEvent) {
        when(event) {
            is SearchEvent.ChangeMode -> {
                _mode.value = event.mode
            }
            is SearchEvent.ChangeSearchText -> {
                _text.value = event.string
            }
            else -> {}
        }
    }
    fun onSearchTextChange(txt: String) {
        _text.value = txt
    }

}