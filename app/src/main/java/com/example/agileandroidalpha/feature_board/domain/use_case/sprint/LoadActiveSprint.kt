package com.example.agileandroidalpha.feature_board.domain.use_case.sprint

import com.example.agileandroidalpha.feature_board.domain.model.SprintWithUsersAndTasks
import com.example.agileandroidalpha.feature_board.domain.repository.RoomRepo
import com.example.agileandroidalpha.feature_board.domain.util.OrderType
import com.example.agileandroidalpha.feature_board.domain.util.TaskOrder
import kotlinx.coroutines.flow.Flow

class LoadActiveSprint(
    private val repository: RoomRepo
) {
    operator fun invoke(
        order: TaskOrder = TaskOrder.Date(OrderType.Descending)
    ) : Flow<SprintWithUsersAndTasks> {
        return repository.getActiveSprint()
    }
}