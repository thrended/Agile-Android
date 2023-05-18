package com.example.agileandroidalpha.feature_board.domain.use_case.sprint

import com.example.agileandroidalpha.feature_board.domain.model.SprintRoom
import com.example.agileandroidalpha.feature_board.domain.model.SprintWithUsersAndTasks
import com.example.agileandroidalpha.feature_board.domain.model.Subtask
import com.example.agileandroidalpha.feature_board.domain.model.Task
import com.example.agileandroidalpha.feature_board.domain.repository.RoomRepo

class DeleteSprint(
    private val repository: RoomRepo
) {
    suspend operator fun invoke(sprint: SprintWithUsersAndTasks) {
        sprint.tasks.forEach { task ->
            repository.deleteSubs(task.subtasks)
            repository.deleteTask(task.task)
        }
        repository.deleteSprint(sprint.sprint)
    }
    suspend fun full(sp: SprintRoom, tasks: List<Task>?, subs: List<Subtask>?) {
        repository.deleteSprint(sp)
        tasks?.let { repository.deleteTasks(tasks) }
        subs?.let{ repository.deleteSubs(subs) }
    }
}