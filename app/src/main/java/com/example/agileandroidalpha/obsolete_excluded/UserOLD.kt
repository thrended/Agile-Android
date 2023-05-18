package com.example.agileandroidalpha.obsolete_excluded

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.ColumnInfo.Companion.TEXT
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date
import kotlin.time.Duration

open class UserFactoryOLD {
    var avatar: Bitmap? = null
}

@Entity(ignoredColumns = ["avatar"])
data class UserOLD(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "userID") val userID: Int?,
    @ColumnInfo(name = "username") val username: String?,
    @ColumnInfo(name = "passwordOLD", typeAffinity = TEXT) val passwordOLD: PasswordOLD? = PasswordOLD("PW"),
    @ColumnInfo(name = "email") val email: String?,
    @ColumnInfo(name = "first_name") val firstName: String?,
    @ColumnInfo(name = "last_name") val lastName: String?,
    @ColumnInfo(name = "acct_type") val userType: String?,
    @ColumnInfo(name = "user_group") val userGroup: String?,
    @ColumnInfo(name = "privilege_lvl") val pLvl: Int?,
    /*@ColumnInfo(name = "user_permissions") val perms: List<UserPermission>?,
    @ColumnInfo(name = "default_view") val default_board: BoardOLD?,
    @ColumnInfo(name = "user_settings") val settings: UserSettings?,
    @ColumnInfo(name = "access") val acc: List<BoardOLD>?,
    @ColumnInfo(name = "visibility") val vis: List<BoardOLD>?,
    @ColumnInfo(name = "owned_boards") val owned: List<BoardOLD>?,
    @ColumnInfo(name = "active_sprint") val sprint: SprintRoom?,
    @ColumnInfo(name = "assigned_tasks") val assTasks: List<Task>?,
    @ColumnInfo(name = "reporter_tasks") val repTasks: List<Task>?,
    @ColumnInfo(name = "assigned_subtasks") val assSubs: List<SubTaskOld>?,
    @ColumnInfo(name = "reporter_subtasks") val repSubs: List<SubTaskOld>?,
    @ColumnInfo(name = "user_lvl") val uLvl: Int?,
    @ColumnInfo(name = "experience") val uXP: Long?,
    @ColumnInfo(name = "to_lvl_up") val tnl: Long?,
    @ColumnInfo(name = "points") val points: Long?,*/
    @ColumnInfo(name = "story_points") val storyPoints: Long?,
    @ColumnInfo(name = "user_points") val userPoints: Long?,
    @ColumnInfo(name = "ip_address") val IPv4: String?,
    @ColumnInfo(name = "hw_address") val hwAdd: String?,
    @ColumnInfo(name = "created_date") val memberSince: Date?,
    @ColumnInfo(name = "last_login") val lastLogin: Date?,
    @ColumnInfo(name = "login_duration") val loginDuration: Duration?,
    @ColumnInfo(name = "active") val isActive: Boolean?,
    @ColumnInfo(name = "admin") val isAdmin: Boolean?,
    @ColumnInfo(name = "banned") val isBanned: Boolean?,
    @ColumnInfo(name = "ban_duration") val banTime: Duration?,
    @ColumnInfo(name = "ban_times") val banTimes: Short?,
    @ColumnInfo(name = "disabled") val isDisabled: Boolean?,
    @ColumnInfo(name = "logged_in") val isLoggedIn: Boolean?,
    @ColumnInfo(name = "muted") val isMuted: Boolean?,
    @ColumnInfo(name = "mute_duration") val muteTime: Duration?,
    @ColumnInfo(name = "restricted") val isRestricted: Boolean?,
    @ColumnInfo(name = "warning_lvl") val warnLvl: Int?,
    @ColumnInfo(name = "num_users") val num_concurrent: Short?,

    ) : UserFactoryOLD()
