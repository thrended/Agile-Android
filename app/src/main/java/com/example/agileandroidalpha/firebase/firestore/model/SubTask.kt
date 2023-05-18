package com.example.agileandroidalpha.firebase.firestore.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

class SubTask(
    var id: Long? = null,
    var uid: String? = null,
    var uri: String? = null,
    var origId: Long? = null,
    var storyId: Long? = null,
    var subId: Long? = null,
    var sid: Long? = null,
    var assId: Long? = null,
    var repId: Long? = null,
    var title: String = "New SubTask",
    var body: String = "New SubTask",
    var desc: String = "A long internal description",
    var dod: String? = null,
    var points: Long = 0,
    var priority: String = "Lowest",
    var color: Int = Color(0xFF81D8D0).toArgb(),
    var labels: List<String>? = null,
    var assignee: String? = null,
    var assUid: String = "",
    var assUri: String? = null,
    var reporter: String? = null,
    var repUid: String = "",
    var repUri: String? = null,
    var resolution: String? = null,
    var current: Boolean = true,
    var active: Boolean = true,
    var cloned: Boolean = false,
    var status: String = "TO DO",
    var done: Boolean = false,
    var type: String = "Subtask",
    var logo: Int? = null,
    var icon: Int? = null,
    var timeToComplete: Long = 0,
    var project: String? = null,
    var component: String? = null,
    var environment: String? = null,
    val creator: String? = null,
    val createdBy: String? = null,
    val creatorID: Long? = null,
    var log: List<String>? = null,
    var comments: List<String>? = null,
    var attachments: List<String>? = null,
    val creDate: Long = System.currentTimeMillis(),
    var modDate: Long = System.currentTimeMillis(),
    var accDate: Long = System.currentTimeMillis(),
) {
    companion object {
        fun create(): SubTask = SubTask()
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