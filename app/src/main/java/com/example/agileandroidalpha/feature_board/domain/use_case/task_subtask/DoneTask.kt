package com.example.agileandroidalpha.feature_board.domain.use_case.task_subtask

import com.example.agileandroidalpha.feature_board.domain.model.InvalidTaskException
import com.example.agileandroidalpha.feature_board.domain.model.TaskAndSubtasks
import com.example.agileandroidalpha.feature_board.domain.repository.RoomRepo

class DoneTask(
    private val repo: RoomRepo
) {
    @Throws(InvalidTaskException::class)
    suspend operator fun invoke(task: TaskAndSubtasks) {
        task.task.done = !task.task.done
        task.task.status = if (task.task.done) "Done" else "In Progress"
        if (task.task.done) {
            task.subtasks.forEachIndexed() { _, subtask ->
                subtask.done = true
                subtask.status = "Done"
            }
        }
        repo.updateTask(task.task)
        repo.updateSubtasks(task.subtasks)
    }
}