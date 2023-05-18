package com.example.agileandroidalpha.feature_board.presentation.backlog

import com.example.agileandroidalpha.feature_board.domain.model.Subtask
import com.example.agileandroidalpha.feature_board.domain.util.SprintOrder
import com.example.agileandroidalpha.firebase.firestore.model.FireUser
import com.example.agileandroidalpha.firebase.firestore.model.Sprint
import com.example.agileandroidalpha.firebase.firestore.model.Story
import com.example.agileandroidalpha.firebase.firestore.model.SubTask

sealed class BacklogEvent {
    data class Archive(val spr: Sprint, val usr: FireUser?): BacklogEvent()
    data class Delete(val spr: Sprint, val sts: List<Story>, val sbs: List<SubTask>?, val deleter: FireUser?): BacklogEvent()
    data class Order(val sprintOrder: SprintOrder): BacklogEvent()
    data class EditSprintStories(val sprint: Sprint, val stories: List<Story>, val subtasks: List<Subtask>?,
                                 val users: List<FireUser>? ): BacklogEvent()
    data class MarkApproved(val spr: Sprint): BacklogEvent()
    data class MarkReviewed(val spr: Sprint): BacklogEvent()
    data class SprintDialogEvent(val spr: Sprint?, val wt: Int = 0, val set: Boolean = false, val hide: Boolean? = null,
                                 val own: Boolean? = null, val manage: Boolean? = null): BacklogEvent()
    data class StoryDialogEvent(val story: Story?, val wt: Int = 0, val set: Boolean = false,
                                val sid: Long? = null, val spr: Sprint? = null): BacklogEvent()
    data class MoveStory(val sto: Story, val spr: Sprint, val id: Long, val pos: Int): BacklogEvent()
    data class RecalculateProgress(val spr: Sprint, val story: Story, val done: Boolean,
                                   val pts: Long, val progress: Triple<Float, Float, Float>,
                                   val rem: Long, val tot: Long): BacklogEvent()
    data class RevokeApproval(val spr: Sprint):  BacklogEvent()
    data class ToggleDone(val spr: Sprint):  BacklogEvent()
    object Refresh: BacklogEvent()
    object Restore: BacklogEvent()
}