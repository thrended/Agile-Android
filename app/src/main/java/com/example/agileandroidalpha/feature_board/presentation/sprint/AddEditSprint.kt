package com.example.agileandroidalpha.feature_board.presentation.sprint

import com.example.agileandroidalpha.feature_board.domain.model.*
import com.example.agileandroidalpha.feature_board.domain.repository.RoomRepo
import java.time.LocalDate
import java.time.Period

class AddEditSprint (
    private val repository: RoomRepo
){
    @Throws(InvalidSprintException::class)
    suspend operator fun invoke(
        sprint: SprintRoom, tasks: List<TaskAndSubtasks>, id: Long?, users: List<User>? = emptyList() )
    {
        if(sprint.title.isBlank()) {
            throw InvalidSprintException("Sprint must have a title.")
        }
        if(sprint.info.startDate < LocalDate.now()) {
            throw InvalidSprintException("Sprint must have a valid start date!")
        }
        if(sprint.info.endDate !in LocalDate.now().plusDays(7)..LocalDate.now().plusDays(70)) {
            throw InvalidSprintException("Sprint must have a valid end date!")
        }
        if( !inRange(sprint.info.duration) ) {
            throw InvalidSprintException("Sprint must have a valid duration between 1 - 10 weeks")
        }
        if(sprint.info.owner == null && sprint.info.manager == null) {
            throw InvalidSprintException("Must associate sprint with a product owner or manager")
        }
        repository.upsertSprint(sprint)
        tasks.forEach { task ->
            repository.upsertTask(task.task)
            repository.upsertSubtasks(task.subtasks)//.map { s -> s.subtask })
        }
        users?.forEach { user ->
            repository.upsertUser(user)
        }
    }

    private fun inRange(x: Period, a: Int = 7, b: Int = 70): Boolean {
        return x.days in a..b
    }
}