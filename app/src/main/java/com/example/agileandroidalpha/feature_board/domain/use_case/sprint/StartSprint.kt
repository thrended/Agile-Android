package com.example.agileandroidalpha.feature_board.domain.use_case.sprint

import com.example.agileandroidalpha.feature_board.domain.model.SprintWithUsersAndTasks
import com.example.agileandroidalpha.feature_board.domain.repository.RoomRepo

class StartSprint(
    private val repository: RoomRepo
){
    suspend operator fun invoke(sprint: SprintWithUsersAndTasks): Long {
        sprint.tasks.forEach { task ->
            repository.upsertTask(task.task)
            repository.upsertSubtasks(task.subtasks)
        }
        return repository.upsertSprint(sprint.sprint)
    }
}