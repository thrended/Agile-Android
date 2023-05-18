package com.example.agileandroidalpha.obsolete_excluded

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

class TaskRepository (private val oldTaskDao: OldTaskDao) {

    val allTasks: Flow<List<TaskOLD>> = oldTaskDao.getTasksByPriorityDesc()
    var nTasks: Int = oldTaskDao.count()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(taskOLD: TaskOLD){
        oldTaskDao.insert(taskOLD)
    }
}