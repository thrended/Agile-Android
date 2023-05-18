package com.example.agileandroidalpha.feature_board.presentation.sprint

import com.example.agileandroidalpha.feature_board.domain.model.SprintRoom
import com.example.agileandroidalpha.feature_board.domain.model.TaskAndSubtasks
import com.example.agileandroidalpha.firebase.firestore.model.FireUser
import com.example.agileandroidalpha.firebase.firestore.model.Sprint

sealed class SprintEvent {
    data class Approve(val sprint: Sprint, val allow: Boolean): SprintEvent()
    data class Archive(val sprint: Sprint, val arch: Boolean): SprintEvent()
    data class ChangeColor(val color: Int): SprintEvent()
    data class ChangeDates(val start: Long, val end: Long, val dur: Int, val cd: Int): SprintEvent()
    data class ChangeDesc(var desc: String): SprintEvent()
    data class ChangeManager(val uid: String, val id: Long, val photo: String?, val name: String,
                           val user: FireUser? = null): SprintEvent()
    data class ChangeOwner(val uid: String, val id: Long, val photo: String?, val name: String,
                           val user: FireUser? = null): SprintEvent()
    data class ChangeResolution(val res: String): SprintEvent()
    data class ChangeStatus(val sta: String): SprintEvent()
    data class ChangeTarget(val tgt: Float): SprintEvent()
    data class ChangeTitle(val title: String): SprintEvent()
    data class CloneSprint(val sprint: Sprint, val tasks: List<TaskAndSubtasks>?): SprintEvent()
    data class EditComment(val str: String, val user: FireUser?): SprintEvent()
    data class ExtendDuration(val len: Int): SprintEvent()
    data class RecalculateProgress(val spr: Sprint, val done: Boolean, val pts: Long): SprintEvent()
    data class RenameSprint(val sprint: SprintRoom, val name: String): SprintEvent()
    data class Refresh(val id: Long): SprintEvent()
    data class Resolve(val res: String): SprintEvent()
    data class Resume(val rem: Int): SprintEvent()
    data class SaveComment(val str: String, val user: FireUser?): SprintEvent()
    data class SaveSprint(val id: Long): SprintEvent()
    data class SetMeetingTime(val hr: Int, val min: Int): SprintEvent()
    data class SetReviewTime(val hr: Int, val min: Int): SprintEvent()
    data class StartPause(val sprint: SprintRoom, val setStarted: Boolean, val days: Int): SprintEvent()
    data class StartPauseTEST(val sprint: Sprint, val setStarted: Boolean, val days: Int): SprintEvent()
    data class ToggleDone(val done: Boolean): SprintEvent()
    object QuickAdd: SprintEvent()
    object Reload: SprintEvent()
    object SyncFromCloud: SprintEvent()
}
