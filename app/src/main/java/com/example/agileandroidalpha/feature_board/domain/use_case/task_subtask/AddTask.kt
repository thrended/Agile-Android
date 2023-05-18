package com.example.agileandroidalpha.feature_board.domain.use_case.task_subtask

import com.example.agileandroidalpha.feature_board.domain.model.*
import com.example.agileandroidalpha.feature_board.domain.repository.RoomRepo

class AddTask(
    private val repo: RoomRepo
) {
    @Throws(InvalidTaskException::class)
    suspend operator fun invoke(
        //sprint: SprintRoom,
        task: Task, subtasks:
        List<Subtask>? = null,
        onRes: (List<Subtask>?) -> Unit
    ): Long {
        if(task.title.isBlank()) {
            throw InvalidTaskException("Must have a title")
        }
        if(task.desc.isBlank()) {
            throw InvalidTaskException("Must have a description")
        }
        if(task.content.isBlank()) {
            throw InvalidTaskException("Must have body content")
        }
//        val x =
//            if(sprint.sprintId == 1L) repo.upsertSprint(sprint)
//            else task.sprintId
//        if(task.sprintId == null || task.sprintId!! != x) {
//            task.sprintId = x
//        }
//        if(task.sprintId!! < 1L) task.sprintId = 1L
        //task.numSubtasks = subtasks?.size ?: 0

        if (!subtasks.isNullOrEmpty()) {
            subtasks.forEach { s -> s.sprintId = task.sprintId }
            repo.upsertSubtasks(subtasks)
        }
        onRes.invoke(subtasks)

        return repo.upsertTask(task)
    }
    suspend fun restore(task: Task, subtasks: List<Subtask>? = null) {
        repo.upsertTask(task)
        if (!subtasks.isNullOrEmpty()) {
            repo.upsertSubtasks(subtasks)//.map {s -> s.subtask } )
        }
    }

}