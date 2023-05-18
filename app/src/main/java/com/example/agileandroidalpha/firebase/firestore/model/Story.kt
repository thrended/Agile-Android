package com.example.agileandroidalpha.firebase.firestore.model

import androidx.compose.ui.graphics.toArgb
import com.example.agileandroidalpha.Priority
import com.example.agileandroidalpha.Status
import com.example.agileandroidalpha.feature_board.domain.model.IssueType
import com.example.agileandroidalpha.ui.theme.AmaranthPink
import com.example.agileandroidalpha.ui.theme.BabyBlue
import com.example.agileandroidalpha.ui.theme.ColdPurple
import com.example.agileandroidalpha.ui.theme.Divine
import com.example.agileandroidalpha.ui.theme.Emerald
import com.example.agileandroidalpha.ui.theme.FireOpal
import com.example.agileandroidalpha.ui.theme.IceColdGreen
import com.example.agileandroidalpha.ui.theme.LBAmaranth
import com.example.agileandroidalpha.ui.theme.LightGreen
import com.example.agileandroidalpha.ui.theme.LightSapphire
import com.example.agileandroidalpha.ui.theme.MistyBlue
import com.example.agileandroidalpha.ui.theme.RedOrange
import com.example.agileandroidalpha.ui.theme.RedPink
import com.example.agileandroidalpha.ui.theme.Simpy
import com.example.agileandroidalpha.ui.theme.TiffanyBlue
import com.example.agileandroidalpha.ui.theme.Turquoise
import com.example.agileandroidalpha.ui.theme.VeryLightGold
import com.example.agileandroidalpha.ui.theme.Violet

class Story(
    var id: Long? = null,
    var uid: String? = null,
    var uri: String? = null,
    var origId: Long? = null,
    var storyId: Long? = null,
    var sid: Long? = null,
    var assId: Long? = null,
    var repId: Long? = null,
    var pid: Long? = null,
    var title: String = "",
    var body: String = "",
    var desc: String = "",
    var dod: String? = null,
    var img: Int? = null,
    var points: Long = 0,
    var priority: String = "High",
    var color: Int = Turquoise.toArgb(),
    var assignee: String? = null,
    var assUid: String = "",
    var assUri: String? = null,
    var reporter: String? = null,
    var repUid: String = "",
    var repUri: String? = null,
    var resolution: String? = null,
    var status: String = "TO DO",
    var done: Boolean = false,
    var labels: List<String>? = null,
    val creDate: Long = System.currentTimeMillis(),
    var modDate: Long = System.currentTimeMillis(),
    var accDate: Long = System.currentTimeMillis(),
    val creator: String? = null,
    val createdBy: String? = null,
    val creatorID: Long? = null,
    var type: String = "Task",
    var logo: Int? = null,
    var icon: Int? = null,
    var current: Boolean = true,
    var active: Boolean = true,
    var cloned: Boolean = false,
    var stories: List<Long?>? = null,
    var subtasks: List<Long?>? = null,
    var timeToComplete: Long = 0,
    var project: String? = null,
    var component: String? = null,
    var versionsAffected: String? = null,
    var versionFixed: String? = null,
    var environment: String? = null,
    var log: List<String>? = null,
    var comments: List<String>? = null
) {
    companion object {
        fun create(): Story = Story()
        var colors = listOf(
            LBAmaranth,
            FireOpal,
            RedOrange,
            VeryLightGold,
            IceColdGreen,
            LightGreen,
            Emerald,
            TiffanyBlue,
            Simpy,
            BabyBlue,
            LightSapphire,
            Divine,
            Violet,
            ColdPurple,
            RedPink,
            AmaranthPink,
            MistyBlue
        )
        var types = listOf(
            IssueType.Story, IssueType.Task, IssueType.Bug, IssueType.Feature, IssueType.Impediment,
            IssueType.Initiative, IssueType.Spike, IssueType.Issue, IssueType.Test, IssueType.Epic,
            IssueType.ChangeRequest, IssueType.Subtask
        )
        var priorities = listOf(
            Priority.Showstopper,
            Priority.Urgent,
            Priority.Critical,
            Priority.Highest,
            Priority.High,
            Priority.Medium,
            Priority.Low,
            Priority.Lowest,
            Priority.Trivial,
            Priority.None
        )
        var statuses = listOf(
            Status.Unknown,
            Status.TO_DO,
            Status.Open,
            Status.MI,
            Status.Stalled,
            Status.InProgress,
            Status.Fixing,
            Status.Reviewing,
            Status.Done,
            Status.Approved,
            Status.Closed,
            Status.Archived
        ).map { s -> s.string}
    }

    fun matchesSearchQuery(que: String): Boolean {
        if (que.isBlank() || que.length < 2) return false
        val matching = listOfNotNull(
            title, desc, uid,
            title.split(" ").first(), desc.split(" ").last(), "id: $id", "id:$id",
            desc.split(" ").first(), desc.split(" ").last(),
            creator?.split(" ")?.first(), creator?.split(" ")?.last(),
            assignee?.split(" ")?.first(), assignee?.split(" ")?.last(),
            reporter?.split(" ")?.first(), reporter?.split(" ")?.last(),
            "sta: $status", "sta:$status",
            "clone : $cloned", "clone:$cloned"
        )
        return matching.any {
            it.contains(que, ignoreCase = true)
        }
    }

}