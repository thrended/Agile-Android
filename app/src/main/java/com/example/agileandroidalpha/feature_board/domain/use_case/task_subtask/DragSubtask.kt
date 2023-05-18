package com.example.agileandroidalpha.feature_board.domain.use_case.task_subtask

import com.example.agileandroidalpha.feature_board.domain.model.Subtask
import com.example.agileandroidalpha.feature_board.domain.model.TaskAndSubtasks
import com.example.agileandroidalpha.feature_board.domain.repository.RoomRepo
import kotlin.math.max

class DragSubtask (
    private val repo: RoomRepo
) {
    suspend operator fun invoke(task: TaskAndSubtasks, subtask: Subtask, stat: String, set: Boolean) {
        subtask.done = set
        subtask.status = stat
        val t = task.task
        val subs = task.subtasks
        var allDone = true
        var doneSubs = 0
        subs.forEachIndexed { _, s ->
            if (!s.done)
            {
                allDone = false
            }
            else { doneSubs += 1 }
        }
        if ( (t.status == "TO DO" && stat != "TO DO") || (t.status == "Done" && !set)) {
            t.status = if(doneSubs.div(max(1, task.subtasks.size)) < 0.5) "In Progress" else "Fixing"
        }
        if (allDone) {
            t.done = true
            t.status = "Done"
        } else if (t.done) {
            t.done = false
            t.status = "Fixing"
        }
        repo.updateSubtask(subtask)
        repo.updateTask(task.task)
    }
}