package com.example.agileandroidalpha.feature_board.domain.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

open class BasicUser {
    val avatar: Int? = null
}

data class UserInfo (
    val email: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val userType: String? = null,
    val userGroup: String? = null,
    val privilegeLvl: Int? = 0,
    val admin: Boolean? = false,
    val active: Boolean = true,
    val online: Boolean? = null,
    val lastLogin: LocalDateTime? = LocalDateTime.now()
)

class UserSettings (
    val theme: Int? = null,
    val home: String? = null,
    val helpOn: Boolean? = true,
    val rememberUser: Boolean? = true
)

@Entity(tableName = "user", ignoredColumns = ["avatar"])
data class User (
    @PrimaryKey(autoGenerate = true) val userId: Long? = null,
    @ColumnInfo(index = true) val username: String = "username",
    val password: String = "password",
    var uid: String? = null,
    //@TypeConverters(Converters::class) val password: Password? = Password("password"),
    @Embedded val info: UserInfo,
    @Embedded val settings: UserSettings
) : BasicUser() {

}

data class UserBrief (
    val userId: Long?,
    val username: String,
    val password: String,
    val email: String?,
    val privilegeLvl: Int?,
    val admin: Boolean?,
    val active: Boolean
)

fun getUserBrief(user: User): UserBrief {
    return UserBrief(
        user.userId,
        user.username,
        user.password,
        user.info.email,
        user.info.privilegeLvl,
        user.info.admin,
        user.info.active
    )
}