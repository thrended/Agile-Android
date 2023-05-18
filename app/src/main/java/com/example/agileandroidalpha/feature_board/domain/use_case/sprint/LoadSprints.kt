package com.example.agileandroidalpha.feature_board.domain.use_case.sprint

import com.example.agileandroidalpha.feature_board.domain.model.SprintRoom
import com.example.agileandroidalpha.feature_board.domain.model.SprintWithUsersAndTasks
import com.example.agileandroidalpha.feature_board.domain.repository.RoomRepo
import com.example.agileandroidalpha.feature_board.domain.util.OrderType
import com.example.agileandroidalpha.feature_board.domain.util.SprintOrder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LoadSprints(
    private val repository: RoomRepo
) {
    operator fun invoke(
        order: SprintOrder = SprintOrder.Default(OrderType.Ascending)
    ) : Flow<List<SprintWithUsersAndTasks>> {
        return repository.getBacklog().map { sprints ->
            when(order.type) {
                is OrderType.Ascending -> {
                    when(order) {
                        is SprintOrder.Default -> sprints.sortedBy { it.sprint.info.startDate }
                        is SprintOrder.ID -> sprints.sortedBy { it.sprint.sprintId }
                        is SprintOrder.Points -> sprints.sortedBy { it.sprint.info.totalPoints }
                        is SprintOrder.Length -> sprints.sortedBy { it.sprint.info.duration.days }
                        is SprintOrder.Epic -> sprints.sortedBy { it.sprint.epicId }
                        else -> sprints.sortedBy { it.users.size }
                    }
                }
                is OrderType.Descending -> {
                    when(order) {
                        is SprintOrder.Default -> sprints.sortedByDescending { it.sprint.info.startDate }
                        is SprintOrder.ID -> sprints.sortedByDescending { it.sprint.sprintId }
                        is SprintOrder.Points -> sprints.sortedByDescending { it.sprint.info.totalPoints }
                        is SprintOrder.Length -> sprints.sortedByDescending { it.sprint.info.duration.days }
                        is SprintOrder.Epic -> sprints.sortedByDescending { it.sprint.epicId }
                        else -> sprints.sortedByDescending { it.users.size }
                    }
                }
            }
        }
    }

    fun basicInfo() : Flow<Map<Long, String>> {
        return repository.getAllSprintBasicInfo().map { sprints ->
            sprints.associate { s -> Pair (s.sprintId!!, s.title) }
        }
    }

    suspend fun list(): List<SprintRoom>? {
        return repository.getSprintsBasicInfo()
    }

    fun update(): Flow<List<SprintRoom>> {
        return repository.getAllSprintBasicInfo()
    }

    suspend fun count() : Int {
        return repository.countSprints()
    }

}