package com.example.agileandroidalpha.feature_board.presentation.add_edit_task

import androidx.compose.ui.focus.FocusState
import com.example.agileandroidalpha.feature_board.domain.model.Subtask
import com.example.agileandroidalpha.feature_board.presentation.add_edit_task.components.TaskSubtaskData
import com.example.agileandroidalpha.firebase.firestore.model.FireUser

sealed class AddEditTaskEvent {
    data class EnteredTitle(val value: String): AddEditTaskEvent()
    data class ChangeTitleFocus(val focusState: FocusState): AddEditTaskEvent()
    data class EnteredDesc(val value: String): AddEditTaskEvent()
    data class ChangeDescFocus(val focusState: FocusState): AddEditTaskEvent()
    data class EnteredBody(val value: String): AddEditTaskEvent()
    data class ChangeBodyFocus(val focusState: FocusState): AddEditTaskEvent()
    data class EnteredDoD(val value: String): AddEditTaskEvent()
    data class ChangeDoDFocus(val focusState: FocusState): AddEditTaskEvent()
    data class ChangePoints(val value: Long): AddEditTaskEvent()
    data class ChangeType(val type: String): AddEditTaskEvent()
    data class ChangePri(val pri: String): AddEditTaskEvent()
    data class ChangeColor(val color: Int): AddEditTaskEvent()
    data class ChangeAssignee(
        val uid: String, val id: Long, val photo: String?, val name: String, val user: FireUser? = null): AddEditTaskEvent()
    data class ChangeAssFocus(val focusState: FocusState): AddEditTaskEvent()
    data class ChangeReporter(
        val uid: String, val id: Long, val photo: String?, val name: String, val user: FireUser? = null): AddEditTaskEvent()
    data class ChangeResolution(val res: String): AddEditTaskEvent()
    data class ChangeSprint(val id: Long): AddEditTaskEvent()
    data class ChangeStatus(val sta: String): AddEditTaskEvent()
    data class ToggleDone(val done: Boolean): AddEditTaskEvent()
    data class AddSubTask(val task: TaskSubtaskData, val sub: Subtask): AddEditTaskEvent()
    data class EditSubTask(val task: TaskSubtaskData, val sub: Subtask): AddEditTaskEvent()
    data class SaveTask(val sprintId: Long, val id: Long): AddEditTaskEvent()
    data class SaveSubTask(val id: Long, val tid: Long, val sid: Long): AddEditTaskEvent()
}