package com.example.agileandroidalpha.feature_board.domain.use_case.task_subtask

import com.example.agileandroidalpha.feature_board.domain.model.Subtask
import com.example.agileandroidalpha.feature_board.domain.model.TaskAndSubtasks
import com.example.agileandroidalpha.feature_board.domain.repository.RoomRepo

class MarkSubtask (
    private val repo: RoomRepo
) {
    suspend operator fun invoke(task: TaskAndSubtasks, subtask: Subtask, set: Boolean) {
        subtask.done = set
        subtask.status = if (subtask.done) "Done" else "TO DO"
        val t = task.task
        val subs = task.subtasks//.map { s -> s.subtask }
        var allDone = true
        if ( (t.status == "TO DO" && subtask.done) || (t.status == "Done" && !subtask.done)) {
            t.status = "In Progress"
        }
        subs.forEachIndexed { _, s ->
            if (!s.done)
            {
                allDone = false
            }
        }
        if (allDone) {
            t.done = true
            t.status = "Done"
        }
        repo.updateSubtask(subtask)
        repo.updateTask(t)
    }
}