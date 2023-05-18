package com.example.agileandroidalpha.feature_board.presentation.edit_subtask

import com.example.agileandroidalpha.feature_board.presentation.add_edit_task.EditTaskState

sealed class EditSubTaskEvent
{
    data class UpdateSubTask(val state: EditTaskState, val id: Long, val tid: Long, val sid: Long): EditSubTaskEvent()
}
