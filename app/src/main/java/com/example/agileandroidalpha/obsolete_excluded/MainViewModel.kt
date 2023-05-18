package com.example.agileandroidalpha.obsolete_excluded

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

const val MAX_USERS = 4

class MainViewModel constructor(
    private val scrumVault: ScrumVault,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) : ViewModel() {

    val boards: List<ScrumModel> = scrumVault.boards
    val tabs: List<ScrumModel> = scrumVault.tabs
    val tasks: List<TaskTmp> = scrumVault.tasks
    val subtasks: List<SubTaskOld> = scrumVault.subtasks

    private val _suggestedTasks = MutableStateFlow<List<ScrumModel>>(emptyList())
    val suggestedTasks: StateFlow<List<ScrumModel>>
        get() = _suggestedTasks

    init {
        _suggestedTasks.value = scrumVault.boards
    }

    fun updatePeople(people: Int) {
        viewModelScope.launch {
            if (people > MAX_USERS) {
                _suggestedTasks.value = emptyList()
            } else {
                val newTasks = withContext(defaultDispatcher) {
                    scrumVault.boards
                        .shuffled(Random(people * (1..100).shuffled().first()))
                }
                _suggestedTasks.value = newTasks
            }
        }
    }

    fun toTabChanged(newTab: String) {
        viewModelScope.launch {
            val newTasks = withContext(defaultDispatcher) {
                scrumVault.boards
                    .filter { it.name.contains(newTab) }
            }
            _suggestedTasks.value = newTasks
        }
    }
}