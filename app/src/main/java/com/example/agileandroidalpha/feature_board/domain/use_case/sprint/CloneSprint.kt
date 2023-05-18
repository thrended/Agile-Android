package com.example.agileandroidalpha.feature_board.domain.use_case.sprint

import com.example.agileandroidalpha.feature_board.domain.model.SprintRoom
import com.example.agileandroidalpha.feature_board.domain.model.Subtask
import com.example.agileandroidalpha.feature_board.domain.model.Task
import com.example.agileandroidalpha.feature_board.domain.model.TaskAndSubtasks
import com.example.agileandroidalpha.feature_board.domain.repository.RoomRepo
import com.example.agileandroidalpha.firebase.firestore.Statics

class CloneSprint(
    private val repository: RoomRepo
) {

    suspend operator fun invoke(sprint: SprintRoom) : Long {
        sprint.info.cloned = true
        return repository.insertSprint(sprint)
    }

    suspend fun cloneFull(tasks: List<TaskAndSubtasks>): Map<Long, List<Long>> {
        val m = mutableMapOf<Long, List<Long>>()

        tasks.forEach { t ->
            t.task.cloned = true
            t.subtasks.forEach { it.cloned = true }
            m[repository.insertTask(t.task)] = repository.insertSubtasks(t.subtasks)
        }

        return m
    }

    suspend fun clone(task: TaskAndSubtasks): Pair< Long, List<Long> > {
        Statics.Cloner.clonedTasks.clear()
        Statics.Cloner.clonedSubtasks.clear()
        task.task.cloned = true
        val x = repository.insertTask(task.task)
        task.subtasks.forEach {
            it.cloned = true
            it.parentId = x
        }
        val y = repository.insertSubtasks(task.subtasks)
        Statics.Cloner.clonedTasks.add(task.task).apply {
            task.task.origId = task.task.taskId
            task.task.taskId = x
        }
        task.subtasks.forEachIndexed { index, subtask ->
            Statics.Cloner.clonedSubtasks.add(subtask).apply {
                subtask.origId = subtask.subId
                subtask.subId = y[index]
            }
        }
        return Pair(
            x,
            y
        )
    }

    suspend fun restore(sp: SprintRoom, tasks: List<Task>?, subtasks: List<Subtask>?) {
        tasks?.forEach {
            repository.upsertTask(it)
        }

            subtasks?.forEach {
                repository.upsertSubtask(it)
            }

    }

}