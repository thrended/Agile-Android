package com.example.agileandroidalpha.feature_board.domain.model

import androidx.compose.ui.graphics.toArgb
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.agileandroidalpha.firebase.firestore.model.Sprint
import java.time.LocalDate
import java.time.LocalTime
import java.time.Period
import kotlin.random.Random

@Entity(tableName = "sprint")
class SprintRoom(
    @PrimaryKey(autoGenerate = true) var sprintId: Long? = null,
    @ColumnInfo(index = true) val title: String = "Sprint ",
    @ColumnInfo val desc: String = "Sprint description ",
    @Embedded val info: SprintInfo,
    val color: Int = Sprint.colors[Random.nextInt(Sprint.colors.size)].toArgb(),
    var active: Boolean = true,
    var target: Float = 0.8f,
    val completed: Boolean = false,
    var origId: Long? = null,
    val projectId: Long? = null,
    val boardId: Long? = null,
    val storyId: Long? = null,
    val epicId: Long? = null,
    var uid: String? = null,
    var uri: String? = null,
    //val numTasks: Long? = 0,
    //val numUsers: Long? = 0
) {
    fun start(
        length: Int = if(!this.info.paused) info.duration.days else this.info.countdown,
    ) {
        val today = LocalDate.now()
        val period = Period.ofDays(length)
        this.info.startDate = today
        this.info.endDate = today + period
        //this.info.countdown = length
        //this.info.meetingTime = meet.atDate(today + Period.ofDays(1))
        this.active = true
        this.info.started = true
        this.info.paused = false
    }

    fun pause() {
        this.info.endDate = LocalDate.MAX
        this.info.meetingTime = LocalTime.MAX
        this.info.reviewTime = LocalTime.MAX
        this.info.started = false
        this.info.paused = true
    }

    fun update(): Boolean {
        if(!active) {
            return false
        }
        val elapsed = Period.between(this.info.startDate, LocalDate.now()).days
        if (elapsed > this.info.elapsed) {
            this.info.elapsed = elapsed
            this.info.countdown = Period.between(LocalDate.now(), this.info.endDate).days
            return true
        }
        return false
    }

    fun markClone(id: Pair<Long, String>) = this.apply {
        info.creatorID = id.first
        uri = id.second
        origId = sprintId
        sprintId = null
        info.cloned = true
    }
}

data class SprintInfo(
    var totalPoints: Long = 0,
    var remPoints: Long = 0,
    var meetingTime: LocalTime? = null,
    var reviewTime: LocalTime? = null,
    var startDate: LocalDate = LocalDate.now(),
    var endDate: LocalDate = LocalDate.MAX,
    var duration: Period = Period.ofDays(21),
    var countdown: Int = duration.days,
    var elapsed: Int = 0,
    var started: Boolean = false,
    var paused: Boolean = false,
    var expired: Boolean = false,
    var new: Boolean = true,
    var cloned: Boolean = false,
    val status: String? = null,
    val progress: Float? = null,
    val progressPct: Float? = null,
    val resolution: String? = null,
    val isApproved: Boolean = false,
    val isArchived: Boolean = false,
    val isReviewed: Boolean = false,
    val manual: Boolean? = null,
    val owner: String? = null,
    val manager: String? = null,
    val creator: String? = null,
    val createdBy: String? = null,
    var creatorID: Long? = null,
    val modDate: Long = System.currentTimeMillis(),
    val userId: Long? = null,
    val backlogWt: Int = 10
)

class InvalidSprintException(message: String): Exception(message)