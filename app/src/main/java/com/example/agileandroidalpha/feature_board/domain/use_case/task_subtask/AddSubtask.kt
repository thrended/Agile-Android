package com.example.agileandroidalpha.feature_board.domain.use_case.task_subtask

import com.example.agileandroidalpha.feature_board.domain.model.Attachment
import com.example.agileandroidalpha.feature_board.domain.model.Subtask
import com.example.agileandroidalpha.feature_board.domain.repository.RoomRepo

class AddSubtask(
private val repo: RoomRepo
) {

    suspend operator fun invoke(
        subtask: Subtask,
        attach: List<Attachment>? = null
    ): Long {
        attach?.let { repo.upsertImages(attach) }
        return repo.upsertSubtask(subtask)
    }

//    suspend fun countImages(): Int {
//        return repo.countAttachments()
//    }

}