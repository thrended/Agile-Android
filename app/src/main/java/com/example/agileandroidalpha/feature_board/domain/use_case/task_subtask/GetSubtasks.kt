package com.example.agileandroidalpha.feature_board.domain.use_case.task_subtask

import com.example.agileandroidalpha.feature_board.domain.model.Subtask
import com.example.agileandroidalpha.feature_board.domain.repository.RoomRepo
import kotlinx.coroutines.flow.Flow

class GetSubtasks (
    private val repository: RoomRepo
)
{
    suspend fun async(
        id: Long
    ): List<Subtask>? {
        return repository.getSubsById(id)
    }

    suspend fun count(): Int {
        return repository.countSubtasks()
    }

    operator fun invoke(
        id: Long,
        //taskOrder: TaskOrder = TaskOrder.Date(OrderType.Descending)
    ): Flow<List<Subtask>> {
        return repository.getSubs(id)
    } //List<Subtask>
        //Flow<TaskWithSubtasks?>
}