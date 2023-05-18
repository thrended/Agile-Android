package com.example.agileandroidalpha.obsolete_excluded

import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class TaskViewModel (private val repo: TaskRepository) : ViewModel() {

    val allTasks: LiveData<List<TaskOLD>> = repo.allTasks.asLiveData()
    var numTasks: Int = repo.nTasks

    init {
        viewModelScope.launch(Dispatchers.IO) {
            repo.allTasks
                .catch { exception -> allTasks.value?.let { emit(it) } }
                .collect {

            }
        }
    }

    fun insert(taskOLD: TaskOLD) = viewModelScope.launch(Dispatchers.IO) {
        repo.insert(taskOLD)
    }
}

class TaskViewModelFactory(private val repo: TaskRepository) : ViewModelProvider.Factory {
    override fun<T : ViewModel> create(modelClass: Class<T>) : T {
        if(modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TaskViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}