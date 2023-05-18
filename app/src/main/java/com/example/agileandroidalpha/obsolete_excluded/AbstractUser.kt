package com.example.agileandroidalpha.obsolete_excluded

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.*
import com.example.agileandroidalpha.R
import java.util.*
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

sealed class AbstractUser(
    username: String = "test",
    passwordOLD: PasswordOLD = PasswordOLD("Test"),
    firstName: String? = "New",
    lastName: String? = "UserOLD",
    avatar: R.drawable? = null,
    privilegeLevel: Int = 0,
    privilegeList: List<UserPermission>? = null,
    userType: String = enumValues<UserLevels>()[privilegeLevel].name,
    defaultView: ViewModel? = null,
    settings: UserSettings? = null,
    access: List<BoardOLD>? = null,
    visibility: List<BoardOLD>? = null,
    userData: UserData? = null,
    assignedTasks: List<TaskTmp>? = null,
    assignedSubTasks: List<SubTaskOld>? = null,
    reporterTasks: List<TaskTmp>? = null,
    reporterSubTasks: List<SubTaskOld>? = null,
    IPv4: String? = "",//Inet4Address.getLocalHost().hostAddress,
    hwAddress: String? = "",
    lastLoginDate: Date = Date(),
    isActive: Boolean = false,
    isAdmin: Boolean = false,
    isBanImmune: Boolean = false,
    isBanned: Boolean = false,
    isBannedPerm: Boolean = false,
    isBannedDuration: Duration = Duration.ZERO,
    isDisabled: Boolean = false,
    isLoggedIn: Boolean = false,
    isMuted: Boolean = false,
    isMutedDuration: Duration = Duration.ZERO,
    isRestricted: Boolean = true,
    numTimesBanned: Short = 0,
    points: Long = 0,
    storyPoints: Long = 0,
    userPoints: Long = 0,
    warningLevel: Int = 0,
    numConcurrentUsers: Short? = 0,

    ) {
    constructor(username: String, passwordOLD: PasswordOLD) : this() {}
    constructor(username: String, passwordOLD: PasswordOLD, privilegeLevel: Int) : this() {}
    constructor(
        username: String,
        passwordOLD: PasswordOLD,
        privilegeLevel: Int,
        privilegeList: List<UserPermission>,
        isActive: Boolean) : this() {}
    constructor( // Admin
        username: String,
        passwordOLD: PasswordOLD,
        privilegeLevel: Int,
        privilegeList: List<UserPermission>,
        userType: String = "Administrator",
        isActive: Boolean = true,
        isAdmin: Boolean = true,
        isBanImmune: Boolean = true,
        isRestricted: Boolean = false) : this() {}
    private constructor(
        username: String,
        passwordOLD: PasswordOLD,
        privilegeLevel: Int,
        access: String,
        visibility: String,
    ) : this() {}
    private val _perms = generateUserPrivileges().toMutableStateList()
    val perms: List<UserPermission>
        get() = _perms
    fun getImmunity(): Boolean { return _immune }
    fun toggleActive() { _active = !_active }
    fun setAdmin(b: Boolean) { _admin = b }
    fun setBanned(b: Boolean, l: Duration) {
        if(!_immune) {
            _banned = b
            _banlength = l
        }
    }
    fun setMuted(b: Boolean, l: Duration) {
        if(!_immune) {
            _muted = b
            _mutelength = l
        }
    }
    fun setDisabled(b: Boolean) { _disabled = b }
    fun setLoggedIn(b: Boolean) { _loggedIn = b }
    fun setBannedPerm(b: Boolean) { _permabanned = b }
    fun setRestricted(b: Boolean) { _restricted = b }
    fun incWarnings(b: Boolean = true, v: Int = 1) {
        if (b) { _warnings += v }
        else { _warnings = 0 }
    }
    fun changePL(increase: Boolean = false) : Boolean {
        var changed: Boolean = true
        if (_pLvl < 1 && !increase || _pLvl > 5 )
            changed = false
        return changed
    }
    val names = enumValues<ActionProperties>().forEach {
        it.name
    }
    private fun generateDefaultPermissions() = List(33) {
        i -> UserPermission(
            name = enumValues<ActionProperties>()[i].name,
            enabled = (i <= 17)
        )
    }
    fun getPerms(un: String) : List<UserPermission>
    {
        return perms
    }
    private fun generateUserPrivileges(lv: Int = 0) = List(33) {
        i -> UserPermission(
            name = enumValues<ActionProperties>()[i].name,
            enabled = (i <= 17)
        )
    }
    fun demote(user: AbstractUser){}
    fun promote(user: AbstractUser){}

    private var _active by mutableStateOf(isActive)
    private var _admin by mutableStateOf(isAdmin)
    private var _banned by mutableStateOf(isBanned)
    private var _banlength by mutableStateOf(isBannedDuration)
    private var _muted by mutableStateOf(isMuted)
    private var _mutelength by mutableStateOf(isMutedDuration)
    private var _disabled by mutableStateOf(isDisabled)
    private var _immune by mutableStateOf(isBanImmune)
    private var _loggedIn by mutableStateOf(isLoggedIn)
    private var _pLvl by mutableStateOf(privilegeLevel)
    private var _permabanned by mutableStateOf(isBannedPerm)
    private var _restricted by mutableStateOf(isRestricted)
    private var _warnings by mutableStateOf(warningLevel)
}

class DefaultUser (
    un: String = "DefaultUser",
    pw: PasswordOLD = PasswordOLD("SIMPLEP4SSW3RD"),
    pLvl: Int = 0,
    pList: List<UserPermission> = List(33) {
            i -> UserPermission(
        name = enumValues<ActionProperties>()[i].name,
        enabled = (i <= 17)
    )
    },
    active: Boolean = true,
        ) : AbstractUser(un, pw, pLvl, pList, active)
{

        }

class Administrator (
    un: String = "adm",
    pw: PasswordOLD = PasswordOLD("$$"+"Super Long PasswordOLD 1234"),
    pLevel: Int = 8,
    pList: List<UserPermission> = List(33) {
            i -> UserPermission(
        name = enumValues<ActionProperties>()[i].name,
        enabled = true
    )
    },
    admin: String = "Administrator",
    adm: Boolean = true,
        ) : AbstractUser(un, pw, pLevel, pList, admin, adm, adm, adm, isRestricted = false)
{
    private fun getUser(username:String): DefaultUser {
        return DefaultUser(username)
    }

    fun banUser(username: String, perm: Boolean = false,
                banDuration: Duration = 60.toDuration(DurationUnit.DAYS),
                ipBan: Boolean = perm)
    {
        if (getUser(username).getImmunity()) { return }
        if (!perm) {
            getUser(username).setBanned(true, banDuration)
        } else {
            getUser(username).setBanned(true, Duration.INFINITE)
        }
        if (ipBan) {
            //process IP ban
        }
        getUser(username).setDisabled(true)
    }

    fun muteUser(username: String, muteDuration: Duration) {
        getUser(username).setMuted(true, muteDuration)
    }

    fun warnUser(name: String, warnValue: Int = 1) {
        getUser(name).incWarnings(v = warnValue)
    }
    fun demote(user: DefaultUser) {
        user.changePL()
    }
    fun promote(user: DefaultUser) {
        user.changePL(true)
    }
}

class UserPermission (
    name: String,
    enabled: Boolean
        )
{

}
enum class ActionProperties {
    View,
    Create,
    Edit,
    Delete,
    AddProject,
    AddBoard,
    AssignSelf,
    Settings,
    Logout,
    Help,
    ResetPassword,
    UpdateTask,
    UpdateSubTask,
    Comment,
    Block,
    Ignore,
    UnIgnore,
    UnBlock,
    UpdateSprint,
    AssignOther,
    RemComment,
    RemProject,
    RemBoard,
    Backup,
    Restore,
    Warn,
    Mute,
    Ban,
    UnMute,
    UnBan,
    Award,
    Promote,
    Demote,
}

enum class UserLevels {
    // USERS
    Restricted,
    Regular,
    Super,
    Elite,
    Lead,
    PO,
    // ADMINS
    Staff,
    Moderator,
    Administrator,
    God
}

@JvmInline value class PasswordOLD(private val s: String)