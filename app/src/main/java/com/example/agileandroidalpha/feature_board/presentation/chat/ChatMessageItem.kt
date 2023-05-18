package com.example.agileandroidalpha.feature_board.presentation.chat

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Announcement
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CommentsDisabled
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.FlagCircle
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.PersonAddDisabled
import androidx.compose.material.icons.filled.PersonOff
import androidx.compose.material.icons.filled.PersonalInjury
import androidx.compose.material.icons.filled.PinDrop
import androidx.compose.material.icons.filled.Reply
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Upgrade
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.agileandroidalpha.R
import com.example.agileandroidalpha.feature_board.presentation.search.components.MiniUserItem
import com.example.agileandroidalpha.firebase.firestore.model.ChatMessage
import com.example.agileandroidalpha.firebase.firestore.model.FireUser
import com.example.agileandroidalpha.ui.theme.BlueZircon
import com.example.agileandroidalpha.ui.theme.Blush
import com.example.agileandroidalpha.ui.theme.Iron
import com.example.agileandroidalpha.ui.theme.MidnightBlue
import com.example.agileandroidalpha.ui.theme.MidnightPurple
import com.example.agileandroidalpha.ui.theme.Mist
import com.example.agileandroidalpha.ui.theme.PlatSilver
import com.example.agileandroidalpha.ui.theme.PurpleHaze
import com.example.agileandroidalpha.ui.theme.SilverWhite
import com.example.agileandroidalpha.ui.theme.Vanilla
import kotlin.random.Random

@Composable
fun ChatMessageItem(
    msg: ChatMessage,
    sender: String,
    pic: String,
    color: Color,
    bgColor: Color,
    modifier: Modifier = Modifier,
    cRad: Dp = 7.5.dp,
    cutoff: Dp = 3.dp,
    user: FireUser? = null,
    profile: FireUser? = null,
    onBanClick: (FireUser, Boolean) -> Unit = { _, _ ->},
    onBlockClick: (FireUser, Boolean) -> Unit = { _, _ ->},
    onDeleteClick: (ChatMessage, Boolean) -> Unit = { _, _ ->},
    onDeletePermClick: (ChatMessage) -> Unit = {},
    onDemoteMessage: (ChatMessage) -> Unit = {},
    onDemoteUser: (FireUser) -> Unit = {},
    onDisableClick: (FireUser, Boolean) -> Unit = { _, _ ->},
    onEditSave: (ChatMessage, String) -> Unit = { _, _ ->},
    onFlagClick: (ChatMessage) -> Unit = {},
    onHideClick: (ChatMessage) -> Unit = {},
    onMoveClick: (ChatMessage) -> Unit = {},
    onMuteClick: (FireUser, Boolean) -> Unit = { _, _ ->},
    onPinClick: (ChatMessage) -> Unit = {},
    onPM: (FireUser, String, String, String) -> Unit = { _, _, _, _ ->},
    onPromoteMessage: (ChatMessage, Long?) -> Unit = { _, _ ->},
    onPromoteUser: (FireUser) -> Unit = {},
    onPurgeUser: (FireUser) -> Unit = {},
    onReportClick: (ChatMessage, FireUser, Boolean) -> Unit = { _, _, _ ->},
    onReplyClick: (ChatMessage, FireUser?) -> Unit = { _, _ ->},
    onRestrictClick: (ChatMessage, FireUser, Boolean) -> Unit = { _, _, _ ->},
    onSilenceClick: (FireUser, Boolean) -> Unit = { _, _ ->},
    onUnFlagClick: (ChatMessage) -> Unit = {},
    onUnHideClick: (ChatMessage) -> Unit = {},
    onUnpinClick: (ChatMessage) -> Unit = {},
    onWarnClick: (ChatMessage, FireUser, Boolean) -> Unit = { _, _, _ ->},
) {
    val isSender = color == Iron
    val decoration = if (msg.isDeleted) TextDecoration.LineThrough else TextDecoration.None
    var dialog by remember {
        mutableStateOf(false)
    }
    var editDialog by remember {
        mutableStateOf(false)
    }
    var editing by remember {
        mutableStateOf(false)
    }
    var editText by remember {
        mutableStateOf(msg.msg)
    }
    var savedBodyText by remember {
        mutableStateOf("")
    }
    var savedTitleText by remember {
        mutableStateOf("")
    }
    var savedSigText by remember {
        mutableStateOf(user?.signature?: "")
    }
    var sendingPM by remember {
        mutableStateOf(false)
    }
    var viewProfile by remember {
        mutableStateOf(false)
    }
    val elevated = user?.isHeadmaster == true || (user?.isAdmin == true && profile?.isHeadmaster == false)
            || (user?.isModerator == true && profile?.isAdmin == false)
    val restrictionCheck = user?.let {
        !user.isRestricted && !user.isWarned && !user.isBanned && !user.isDisabled && !user.isMuted
    }?: false

    user?.let {
        if (viewProfile) {
            Dialog(onDismissRequest = { viewProfile = !viewProfile }) {
                Column(
                    modifier = modifier
                        .fillMaxSize()
                        .background(Blush),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    profile?.let {
                        MiniUserItem(
                            ur = profile,
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(0.6f)
                                .wrapContentSize(Alignment.Center),
                            hMultiplier = 2.5f
                        )
                        if (user.isHeadmaster || (user.isAdmin && (user == profile || !profile.isAdmin))) {
                            FilledTonalButton(
                                enabled = !profile.isBanned,
                                onClick = {
//                                    viewProfile = !viewProfile
                                    sendingPM = !sendingPM
                                },
                            ) {
                                Text(text = "Private Message")
                                Icon(
                                    imageVector = Icons.Default.Message,
                                    contentDescription = "View"
                                )
                            }
                            FilledTonalButton(
                                onClick = {
                                    onPromoteUser(profile)
                                    viewProfile = !viewProfile
                                },
                            ) {
                                Text(text = "Promote User")
                                Icon(
                                    imageVector = Icons.Default.Upgrade,
                                    contentDescription = "Promote"
                                )
                            }
                            FilledTonalButton(
                                onClick = {
                                    onDemoteUser(profile)
                                    viewProfile = !viewProfile
                                },
                            ) {
                                Text(text = "Demote User")
                                Icon(
                                    imageVector = Icons.Default.ArrowDownward,
                                    contentDescription = "Demote"
                                )
                            }
                            val banned = profile.isBanned
                            val banText = if (banned) "Unban User" else "Ban User"
                            FilledTonalButton(
                                enabled = (msg.senderUid != user.uid) &&
                                    ((user.isHeadmaster) || (user.isAdmin && profile.isWarned) ||
                                    (elevated && profile.isRestricted)),
                                onClick = {
                                    onBanClick(profile, banned)
                                    dialog = !dialog
                                },
                            ) {
                                Text( text = banText )
                                Icon(
                                    imageVector = Icons.Default.PersonalInjury,
                                    contentDescription = "Ban"
                                )
                            }
                            val disabled = profile.isDisabled
                            val disText = if (disabled) "Restore User" else "Disable User"
                            FilledTonalButton(
                                enabled = (msg.senderUid != user.uid) &&
                                    ((user.isHeadmaster) || (user.isAdmin && profile.isWarned) ||
                                    (elevated && profile.isRestricted)),
                                onClick = {
                                    onDisableClick(profile, disabled)
                                    dialog = !dialog
                                },
                            ) {
                                Text( text = disText )
                                Icon(
                                    imageVector = Icons.Default.PersonAddDisabled,
                                    contentDescription = "Ban"
                                )
                            }
                            FilledTonalButton(
                                enabled = (msg.senderUid != user.uid) &&
                                        ((user.isHeadmaster) || (user.isAdmin && profile.isBanned) ||
                                                (user.isAdmin && profile.isDisabled)),
                                onClick = {
                                    onPurgeUser(profile)
                                    dialog = !dialog
                                },
                            ) {
                                Text( text = "Purge User (Permanent)" )
                                Icon(
                                    imageVector = Icons.Default.PersonOff,
                                    contentDescription = "Purge"
                                )
                            }
                        }
                    }
                    FilledTonalButton(
                        onClick = {
                            viewProfile = !viewProfile
                        },
                    ) {
                        Text(text = "Close")
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close"
                        )
                    }
                }
            }
        }
        else if (sendingPM) {
            profile?.let {
                Column(
                    modifier = modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    TextField(
                        modifier = modifier.fillMaxHeight(0.15f),
                        value = savedTitleText,
                        onValueChange = { savedTitleText = it }
                    )
                    Spacer(modifier.fillMaxHeight(0.025f))
                    TextField(
                        modifier = modifier.fillMaxHeight(0.5f),
                        value = savedBodyText,
                        onValueChange = { savedBodyText = it }
                    )
                    Spacer(modifier.fillMaxHeight(0.025f))
                    TextField(
                        modifier = modifier.fillMaxHeight(0.15f),
                        value = savedSigText,
                        onValueChange = { savedSigText = it }
                    )
                    Spacer(modifier.fillMaxHeight(0.025f))
                    FilledTonalButton(
                        enabled = !profile.isBanned,
                        onClick = {
                            onPM(profile, savedTitleText, savedBodyText, savedSigText)
                            dialog = !dialog
                            sendingPM = !sendingPM
                        },
                    ) {
                        Text(text = "Send Private Message")
                    }
                }
            }
        }
        else if (editDialog) {
            Dialog(onDismissRequest = { editDialog = !editDialog }) {
                Column(
                    modifier = modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    FilledTonalButton(
                        enabled = elevated || user.isPowerUser || (msg.senderUid != user.uid && restrictionCheck),
                        onClick = {
                            onReplyClick(msg, profile)
                            editDialog = !editDialog
                        },
                    ) {
                        Text(text = "Reply")
                        Icon(
                            imageVector = Icons.Default.Reply,
                            contentDescription = "Reply"
                        )
                    }
                    FilledTonalButton(
                        enabled = elevated || (msg.senderUid == user.uid && restrictionCheck),
                        onClick = {
                            editing = !editing
                            editDialog = !editDialog
                        },
                    ) {
                        Text(text = "Edit Message")
                        Icon(
                            imageVector = Icons.Default.EditNote,
                            contentDescription = "Edit"
                        )
                    }
                    val deleted = msg.isDeleted
                    val deleteText = if (!deleted) "Delete Message" else "Restore Message"
                    FilledTonalButton(
                        enabled = elevated || (msg.senderUid == user.uid && restrictionCheck),
                        onClick = {
                            onDeleteClick(msg, msg.isDeleted)
                            editDialog = !editDialog
                        },
                    ) {
                        Text(text = deleteText)
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = "Delete"
                        )
                    }
                    FilledTonalButton(
                        enabled = elevated || (deleted && msg.senderUid == user.uid && restrictionCheck),
                        onClick = {
                            onDeletePermClick(msg)
                            editDialog = !editDialog
                        },
                    ) {
                        Text(text = "Delete Forever")
                        Icon(
                            imageVector = Icons.Default.DeleteForever,
                            contentDescription = "Delete"
                        )
                    }
                    val announcementDur = if (!msg.isAnnouncement) 99999L else null
                    val annexTxt = if (!msg.isAnnouncement) "Promote Message" else "Demote Message"
                    FilledTonalButton(
                        enabled = elevated || (user.isPowerUser && msg.senderUid == user.uid),
                        onClick = {
                            if (msg.isAnnouncement)
                                onDemoteMessage(msg)
                            else
                                onPromoteMessage(msg, announcementDur)
                            editDialog = !editDialog
                        },
                    ) {
                        Text(text = annexTxt)
                        Icon(
                            imageVector = Icons.Default.Announcement,
                            contentDescription = "Announcement"
                        )
                    }
                    val flagText = if (!msg.isFlagged) "Flag Message" else "Remove Flag"
                    FilledTonalButton(
                        enabled = elevated || user.isVerified || msg.senderUid == user.uid,
                        onClick = {
                            if (msg.isFlagged)
                                onUnFlagClick(msg)
                            else
                                onFlagClick(msg)
                            editDialog = !editDialog
                        },
                    ) {
                        Text(text = flagText)
                        Icon(
                            imageVector = Icons.Default.FlagCircle,
                            contentDescription = "Flag"
                        )
                    }
                    val hidText = if (!msg.isHidden) "Hide Message" else "Show Message"
                    FilledTonalButton(
                        enabled = elevated || user.isVerified || msg.senderUid == user.uid,
                        onClick = {
                            if (msg.isHidden)
                                onUnHideClick(msg)
                            else
                                onHideClick(msg)
                            editDialog = !editDialog
                        },
                    ) {
                        Text(text = hidText)
                        Icon(
                            imageVector = Icons.Default.VisibilityOff,
                            contentDescription = "Hide"
                        )
                    }
                    val pinText = if (!msg.isPinned) "Pin Message" else "Unpin Message"
                    FilledTonalButton(
                        enabled = elevated || user.isPowerUser || msg.senderUid == user.uid,
                        onClick = {
                            if (msg.isPinned)
                                onUnpinClick(msg)
                            else
                                onPinClick(msg)
                            editDialog = !editDialog
                        },
                    ) {
                        Text(text = pinText)
                        Icon(
                            imageVector = Icons.Default.PinDrop,
                            contentDescription = "Pin"
                        )
                    }
                    FilledTonalButton(
                        onClick = {
                            editDialog = !editDialog
                        },
                    ) {
                        Text(text = "Close")
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close"
                        )
                    }
                }
            }
        }
        else if (dialog) {
            Dialog(onDismissRequest = { dialog = !dialog }) {
                Column(
                    modifier = modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    profile?.let {
                        FilledTonalButton(
                            onClick = {
                                viewProfile = !viewProfile
                            },
                        ) {
                            Text(text = "View Profile")
                            Icon(
                                imageVector = Icons.Default.SupportAgent,
                                contentDescription = "View"
                            )
                        }
                        FilledTonalButton(
                            enabled = !profile.isBanned,
                            onClick = {
                                dialog = !dialog
                                sendingPM = !sendingPM
                            },
                        ) {
                            Text(text = "Private Message")
                            Icon(
                                imageVector = Icons.Default.Message,
                                contentDescription = "View"
                            )
                        }
                        FilledTonalButton(
                            enabled = user.isHeadmaster || (user.isAdmin && (user == profile || !profile.isAdmin)),
                            onClick = {
                                dialog = !dialog
                                onPromoteUser(profile)
                            },
                        ) {
                            Text(text = "Promote User")
                            Icon(
                                imageVector = Icons.Default.Upgrade,
                                contentDescription = "Promote"
                            )
                        }
                        FilledTonalButton(
                            enabled = user.isHeadmaster || (user.isAdmin && (user == profile || !profile.isAdmin)),
                            onClick = {
                                dialog = !dialog
                                onDemoteUser(profile)
                            },
                        ) {
                            Text(text = "Demote User")
                            Icon(
                                imageVector = Icons.Default.ArrowDownward,
                                contentDescription = "Demote"
                            )
                        }
                        val blocked = user.blockedUsers?.contains(profile.uid) == true
                        val blockText = if (blocked) "Unblock User" else "Block User"
                        FilledTonalButton(
                            enabled = !profile.isHeadmaster && !profile.isAdmin && profile.isModerator
                                    && profile != user,
                            onClick = {
                                onBlockClick(profile, blocked)
                                dialog = !dialog
                            },
                        ) {
                            Text( text = blockText )
                            Icon(
                                imageVector = Icons.Default.Block,
                                contentDescription = "Block"
                            )
                        }
                        val muted = profile.isMuted
                        val muteText = if (muted) "Unmute User" else "Mute User"
                        FilledTonalButton(
                            enabled = elevated && msg.senderUid != user.uid,
                            onClick = {
                                onMuteClick(profile, muted)
                                dialog = !dialog
                            },
                        ) {
                            Text( text = muteText )
                            Icon(
                                imageVector = Icons.Default.PersonAddDisabled,
                                contentDescription = "Ban"
                            )
                        }
                        val silenced = profile.isSilenced
                        val silenceText = if (!silenced) "Silence User" else "Un-Silence User"
                        FilledTonalButton(
                            enabled = elevated && msg.senderUid != user.uid,
                            onClick = {
                                onSilenceClick(profile, silenced)
                                dialog = !dialog
                            },
                        ) {
                            Text(text = silenceText)
                            Icon(
                                imageVector = Icons.Default.CommentsDisabled,
                                contentDescription = "Silence"
                            )
                        }
                        val reported = profile.isWarned
                        val reportText = if (elevated) {
                            if (!reported) "Warn User"
                            else "Remove Warning"
                        } else "Report User"
                        FilledTonalButton(
                            enabled = elevated || msg.senderUid != user.uid,
                            onClick = {
                                if (elevated) onWarnClick(msg, profile, reported)
                                else onReportClick(msg, profile, reported)
                                dialog = !dialog
                            },
                        ) {
                            Text(text = reportText)
                            Icon(
                                imageVector = Icons.Default.WarningAmber,
                                contentDescription = "Warn"
                            )
                        }
                        val restricted = profile.isRestricted
                        val restrictText = if (elevated) {
                            if (!restricted) "Restrict User"
                            else "Remove Restrictions"
                        } else "Action Disabled"
                        FilledTonalButton(
                            enabled = elevated && msg.senderUid != user.uid && user.isWarned,
                            onClick = {
                                onRestrictClick(msg, profile, restricted)
                                dialog = !dialog
                            },
                        ) {
                            Text(text = restrictText)
                            Icon(
                                imageVector = Icons.Default.WarningAmber,
                                contentDescription = "Warn"
                            )
                        }
                        FilledTonalButton(
                            onClick = {
                                dialog = !dialog
                            },
                        ) {
                            Text(text = "Close")
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close"
                            )
                        }
                    }
                }
            }
        }
        else {

        }
    }

    Box(
        modifier = modifier
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val clipPath = Path().apply {
                lineTo(size.width - cutoff.toPx(), 0f)
                lineTo(size.width, cutoff.toPx())
                lineTo(size.width, size.height)
                lineTo(0f, size.height)
                close()
            }

            clipPath(clipPath) {
                drawRoundRect(
                    color = FireUser.colors[Random.nextInt(FireUser.colors.size)],
                    size = size,
                    cornerRadius = CornerRadius(cRad.toPx())
                )
//                drawRoundRect(
//                    color = Color(
//                        ColorUtils.blendARGB(TiffanyBlue.toArgb(), PastelLilac.toArgb(), 0.5f)
//                    ),
//                    topLeft = Offset(size.width / 2.25f, 15f),
//                    size = Size(175f, 125f),
//                    //size = Size(125f, 75f),
//                    cornerRadius = CornerRadius(cRad.toPx())
//                )
            }
        }
        val name = sender.ifBlank { null }
        val photo = pic.ifBlank { null }
        val reply = if (msg.isReply) msg.replyTxt else null
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround
        ){
            reply?.let {
                Text(
                    text = reply,
                    color = MidnightPurple,
                    fontSize = 10.sp,
                    modifier = modifier
                        .background(Vanilla)
                        .align(Alignment.CenterHorizontally)
                )
            }
            Text(
                text = name?: "Anonymous",
                color = color,
                fontSize = 10.sp,
                modifier = modifier
                    .background(PlatSilver)
                    .align(Alignment.CenterHorizontally)
            )
            Text(
                text = msg.timestamp,
                color = MidnightBlue,
                fontSize = 8.sp,
                modifier = modifier
                    .background(PlatSilver)
                    .align(Alignment.CenterHorizontally)
            )
            Spacer(Modifier.height(2.5.dp))
            val nm = msg.receiver?: "Unknown"
            val txt = msg.extraTxt.ifBlank { "(Private to $nm) " }
            if (msg.isPrivate || msg.extraTxt.isNotBlank()) {
                Text(
                    text = txt,
                    color = Mist,
                    textDecoration = decoration,
                    fontWeight = Bold,
                    fontSize = 12.sp,
                    modifier = modifier
                        .offset(x = 10.dp)
                        .background(BlueZircon)
                        .align(Alignment.CenterHorizontally)
                )
                Spacer(Modifier.height(2.5.dp))
            }
            Spacer(Modifier.height(2.5.dp))
            if (msg.title.isNotBlank() && msg.extraTxt.isNotBlank()) {
                Text(
                    text = msg.title,
                    color = Mist,
                    textDecoration = decoration,
                    fontWeight = Bold,
                    fontSize = 12.sp,
                    modifier = modifier
                        .offset(x = 10.dp)
                        .background(BlueZircon)
                        .align(Alignment.CenterHorizontally)
                )
                Spacer(Modifier.height(2.5.dp))
            }
            Row(
                modifier = modifier,
                horizontalArrangement = if(isSender) Arrangement.End else Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!isSender){
                    photo?.let {
                        AsyncImage(
                            model = photo,
                            contentDescription = "Profile picture",
                            modifier = Modifier
                                .offset(x = (-10).dp, y = 0.dp)
                                .size(33.dp)
                                .clip(CircleShape)
                                .clickable {
                                    dialog = !dialog
                                },
                            contentScale = ContentScale.Crop,
                            error = painterResource(id = R.drawable.profsad_small)
                        )
                    }
                }
                else {
                    msg.receiverPic?.let {
                        AsyncImage(
                            model = msg.receiverPic,
                            contentDescription = "Profile picture",
                            modifier = Modifier
                                .offset(x = (-10).dp, y = 0.dp)
                                .size(17.dp)
                                .clip(CircleShape)
                                .clickable {
                                    dialog = !dialog
                                },
                            contentScale = ContentScale.Crop,
                            error = painterResource(id = R.drawable.profsad_small)
                        )
                    }
                }
                if (editing)
                {
                    TextField(
                        value = editText,
                        label = {Text("Edit Message")},
                        onValueChange = {
                            editText = it
                        },
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    onEditSave(msg, editText)
                                    editing = !editing
                                }
                            ) {
                                Icon(imageVector = Icons.Default.EditNote, contentDescription = null)
                            }
                        }
                    )
                } else {
                    Text(
                        text = msg.msg,
                        color = color,
                        fontSize = 12.sp,
                        fontStyle = if (msg.isSecret) FontStyle.Italic else FontStyle.Normal,
                        textDecoration = decoration,
                        modifier = modifier
                            .background(bgColor)
                    )
                    if (msg.isEdited) {
                        Icon(
                            modifier = modifier
                                .scale(0.5f),
                            imageVector = Icons.Default.Edit,
                            contentDescription = null
                        )
                    }
                }
                if (isSender) {
                    photo?.let {
                        AsyncImage(
                            model = photo,
                            contentDescription = "Profile picture",
                            modifier = Modifier
                                .offset(x = (45).dp, y = (-15).dp)
                                .size(33.dp)
                                .clip(CircleShape)
                                .clickable {
                                    dialog = !dialog
                                },
                            contentScale = ContentScale.Crop,
                            error = painterResource(id = R.drawable.profsad_small)
                        )
                    }
//                    name?.let {
//                        Text(
//                            text = sender,
//                            color = color,
//                            fontSize = 8.sp,
//                            modifier = modifier
//                                .background(PlatSilver)
//                        )
//                    }
                }
                else {
                    msg.receiverPic?.let {
                        AsyncImage(
                            model = msg.receiverPic,
                            contentDescription = "Profile picture",
                            modifier = Modifier
                                .offset(x = (45).dp, y = (-15).dp)
                                .size(17.dp)
                                .clip(CircleShape)
                                .clickable {
                                    dialog = !dialog
                                },
                            contentScale = ContentScale.Crop,
                            error = painterResource(id = R.drawable.profsad_small)
                        )
                    }
                }
            }
            if (msg.signature.isNotBlank()) {
                val sigText = if (msg.signature.contains("-") || msg.signature.contains("~")) ""
                            else "- "
                Text(
                    text = sigText + msg.signature,
                    color = PurpleHaze,
                    textDecoration = decoration,
                    fontStyle = FontStyle.Italic,
                    fontSize = 10.sp,
                    modifier = modifier
                        .background(SilverWhite)
                        .align(Alignment.CenterHorizontally)
                        .offset(x = 60.dp)
                )
                Spacer(Modifier.height(2.5.dp))
            }
        }
        IconButton(
            onClick = { editDialog = !editDialog },
            modifier = Modifier.align(Alignment.BottomEnd)
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit Options",
                tint = MaterialTheme.colors.onSurface
            )
        }
    }
}