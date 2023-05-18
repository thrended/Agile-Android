@file:OptIn(ExperimentalMaterialApi::class)

package com.example.agileandroidalpha.feature_board.presentation.chat

import android.widget.Toast
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetState
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FabPosition
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarResult
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Anchor
import androidx.compose.material.icons.filled.Announcement
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.DisabledVisible
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.HideSource
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.PersonOff
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.ScheduleSend
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SendAndArchive
import androidx.compose.material.icons.filled.SupervisedUserCircle
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.agileandroidalpha.core.BottomNavBar
import com.example.agileandroidalpha.core.DroidDrawer
import com.example.agileandroidalpha.core.TopAppBar
import com.example.agileandroidalpha.firebase.firestore.PrivateMessage
import com.example.agileandroidalpha.firebase.firestore.Statics.Cache.chatLog
import com.example.agileandroidalpha.firebase.firestore.model.ChatMessage
import com.example.agileandroidalpha.firebase.firestore.model.FireUser
import com.example.agileandroidalpha.ui.theme.BlueDiamond
import com.example.agileandroidalpha.ui.theme.BlueJay
import com.example.agileandroidalpha.ui.theme.BlueZircon
import com.example.agileandroidalpha.ui.theme.DarkBlueGray
import com.example.agileandroidalpha.ui.theme.DarkGold
import com.example.agileandroidalpha.ui.theme.DarkSlate
import com.example.agileandroidalpha.ui.theme.Grapefruit
import com.example.agileandroidalpha.ui.theme.GrayGoose
import com.example.agileandroidalpha.ui.theme.Iron
import com.example.agileandroidalpha.ui.theme.Lotus
import com.example.agileandroidalpha.ui.theme.MidnightBlue
import com.example.agileandroidalpha.ui.theme.Saffron
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.min
import kotlin.random.Random

@ExperimentalMaterialApi
@Composable
fun ChatScreen(
    navController: NavController,
    state: ChatState,
    log: ChatLogState,
    user: FireUser?,
    roomNo: Int,
    onEvent: (ChatEvent) -> Unit,
    eventFlow: SharedFlow<ChatViewModel.UIEvent>,
    modifier: Modifier = Modifier,
) {
    val bottomScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = BottomSheetState(
            initialValue = BottomSheetValue.Collapsed
        )
    )
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val me = user?.name?: user?.email?: "Anonymous"
    var sheetState by remember {
        mutableStateOf(0)
    }
    var private by rememberSaveable {
        mutableStateOf(false)
    }
    var replying by remember {
        mutableStateOf(false)
    }
    var replyText by remember {
        mutableStateOf("")
    }
    var replyTitle by remember {
        mutableStateOf("")
    }
    var replySig by remember {
        mutableStateOf("")
    }
    var secret by rememberSaveable {
        mutableStateOf(false)
    }
    var alias by rememberSaveable {
        mutableStateOf("Secret User")
    }
    var signed by rememberSaveable {
        mutableStateOf(false)
    }
    var sig by remember {
        mutableStateOf(
            "- ${user?.signature ?: user?.name ?: user?.email ?: "????"}"
        )
    }
    var dialog by remember {
        mutableStateOf(false)
    }
    var announcing by remember {
        mutableStateOf(false)
    }
    var announceTitle by remember {
        mutableStateOf("")
    }
    var announceText by remember {
        mutableStateOf("")
    }
    var announceSig by remember {
        mutableStateOf( if (secret) "" else user?.signature?: "" )
    }

    LaunchedEffect(key1 = true) {
        eventFlow.collectLatest { event ->
            when (event) {
                is ChatViewModel.UIEvent.ToastMsg -> {
                    Toast.makeText(
                        context,
                        event.msg,
                        event.dur
                    ).show()
                }
                else -> {}
            }
        }
    }

    LaunchedEffect(key1 = state.markForUpdate) {
        delay(750L)
        state.markForUpdate = false
        delay(750L)
    }

    BottomSheetScaffold(
        sheetContent = {
            user?.let {
                Box(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (sheetState == 0) "Switch Chat Room View" else "Select User"
                    )
                }
                if(bottomScaffoldState.bottomSheetState.isCollapsed) {
                    Spacer(Modifier.fillMaxHeight(0.02250f))
                }
                when (sheetState) {
                    0 -> {
                        LazyColumn(
                            content = {
                                itemsIndexed(
                                    listOf(
                                        "Chat Room",
                                        "Private Chat",
                                        "Announcements"
                                    )
                                ) { i, ch ->
                                    val col = if (roomNo == i + 1) {
                                        DarkGold
                                    } else {
                                        BlueJay
                                    }
                                    val current = if (roomNo == i + 1) {
                                        " [Current] "
                                    } else {
                                        ""
                                    }
                                    ListItem(
                                        modifier = Modifier
                                            .clickable(
                                                enabled = !user.isBanned && !user.isDisabled
                                            ) {
                                                scope.launch {
                                                    onEvent(
                                                        ChatEvent.ChangeChatRoom(i + 1)
                                                    )
                                                    Toast.makeText(
                                                        context,
                                                        "Switching to $ch.",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                    bottomScaffoldState.bottomSheetState.collapse()
                                                }
                                            },
                                        text = {
                                            Text(
                                                text = ch + current,
                                                color = col
                                            )
                                        }
                                    )
                                }
                            }
                        )
                    }

                    else -> {
                        val list = state.availUsers.toMutableList().apply {
                            add(FireUser(name = "All (View only)"))
                        }
                        LazyColumn(
                            content = {
                                itemsIndexed(list) { i, ur ->
                                    val name = ur.name ?: ur.email ?: "Anonymous User"
                                    val col = if (ur.isHeadmaster) {
                                        Saffron
                                    } else if (ur.isAdmin) {
                                        Grapefruit
                                    } else if (ur.isModerator) {
                                        Lotus
                                    } else if (ur.isPowerUser) {
                                        BlueZircon
                                    } else {
                                        DarkSlate
                                    }
                                    val current = if (log.selectedUser == ur) {
                                        " [Current] "
                                    } else {
                                        ""
                                    }
                                    ListItem(
                                        modifier = Modifier
                                            .clickable(
                                                enabled = user != ur && !user.isBanned && !user.isDisabled
                                            ) {
                                                scope.launch {
                                                    onEvent(
                                                        ChatEvent.ChangePrivateMsgUser(ur)
                                                    )
                                                    Toast.makeText(
                                                        context,
                                                        "Viewing Conversation with $name ",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                    bottomScaffoldState.bottomSheetState.collapse()
                                                }
                                            },
                                        text = {
                                            Text(
                                                text = name + current,
                                                color = col
                                            )
                                        }
                                    )
                                }
                            }
                        )
                    }
                }
            }
        },
        scaffoldState = bottomScaffoldState
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = when(roomNo) {
                        1 -> "Chat Room"
                        2 -> "Private Chat"
                        3 -> "Announcements"
                        else -> ""
                    },
                    icon = Icons.Filled.Chat,
                    onIconClick = {
                        scope.launch {
                            scaffoldState.drawerState.open()
                        }
                    },
                    icon2 = if (user != null && user.isAdmin) Icons.Filled.SupervisedUserCircle
                    else if (user != null) Icons.Filled.PersonOutline
                    else Icons.Filled.PersonOff,
                    onIcon2Click = {
                        signed = !signed
                        val bool = if (signed) "ON" else "OFF"
                        Toast.makeText(
                            context,
                            "Signatures $bool",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    icon3 = if (roomNo != 2) Icons.Filled.Announcement else Icons.Filled.SwapVert,//Icons.Filled.CallToAction,
                    onIcon3Click =
                    {
                        when (roomNo) {
                            2 -> {
                                scope.launch {
                                    sheetState = 1
                                    private = true
                                    if (bottomScaffoldState.bottomSheetState.isCollapsed)
                                        bottomScaffoldState.bottomSheetState.expand()
                                    else bottomScaffoldState.bottomSheetState.collapse()
                                }
                            }
                            else -> {
                                sheetState = 0
                                private = false
                                if (user?.isModerator == false && !user.isAdmin) {
                                    Toast.makeText(
                                        context,
                                        "Announcement privileges are only granted to " +
                                                "Moderators, Administrators and Headmasters",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    announcing = !announcing
                                }
                            }
                        }
                    },
                    icon4 = if (roomNo != 2) Icons.Filled.SwapHoriz else Icons.Filled.CompareArrows,
                    onIcon4Click =
                    {
                        scope.launch {
                            sheetState = 0
                            if (bottomScaffoldState.bottomSheetState.isCollapsed)
                                bottomScaffoldState.bottomSheetState.expand()
                            else bottomScaffoldState.bottomSheetState.collapse()
                        }
                    },
                    icon5 = if (user?.isAdmin == true || user?.isHeadmaster == true) Icons.Filled.DeleteForever
                            else null,
                    onIcon5Click = {
                        scope.launch {
                            onEvent(ChatEvent.DeleteAllMarked)
                            val result = scaffoldState.snackbarHostState.showSnackbar(
                                message = "All Messages Marked for Deletion have been Purged",
                                actionLabel = "Undo"
                            )
                            if (result == SnackbarResult.ActionPerformed) {
                                onEvent(ChatEvent.UndoDelete)
                            }
                        }
                    },
                    icon6 = if (!secret) Icons.Default.DisabledVisible else Icons.Default.Visibility,
                    onIcon6Click = {
                        if (user?.isAdmin == false) {
                            Toast.makeText(
                                context,
                                "Secret Messaging is only available to Administrator level users",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            secret = !secret
                            if (secret) {
                                dialog = !dialog
                                Toast.makeText(
                                    context,
                                    "Secret Alias Activated",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    context,
                                    "Secret Alias Turned Off",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    },
                    title2 = null,
                    title3 = null
                )

            },
            drawerContent = {
                DroidDrawer(
                    //currentScreen = Screen.TasksScreen.route,
                    navController = navController,
                    closeDrawerAction = {
                        // here - Drawer close
                        scope.launch {
                            scaffoldState.drawerState.close()
                        }
                    },
                    clickDrawerAction = {
                        scope.launch {
                            scaffoldState.drawerState.close()
                            navController.navigate(it)
                        }
                    }
                )
            },
            floatingActionButton = {        // Replaced by Top Bar Action
//                FloatingActionButton(
//                    onClick = {
//                        if (user?.isAdmin == false) {
//                            Toast.makeText(
//                                context,
//                                "Secret Messaging is only available to Administrator level users",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        } else {
//                            secret = !secret
//                            if (secret) {
//                                dialog = !dialog
//                                Toast.makeText(
//                                    context,
//                                    "Secret Alias Activated",
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                            } else {
//                                Toast.makeText(
//                                    context,
//                                    "Secret Alias Turned Off",
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                            }
//                        }
//                    },
//                    backgroundColor = MaterialTheme.colors.primary,
//                    modifier = Modifier
//                        .displayCutoutPadding()
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.DisabledVisible,
//                        contentDescription = "Secret Chat"
//                    )
//                }
            },
            floatingActionButtonPosition = FabPosition.End,
            bottomBar = {
                BottomNavBar(
                    navController = navController,
                    onBarClicked = {
                        navController.navigate(it.route)
                    },
                )
            },
            scaffoldState = scaffoldState,
        ) { padding ->
            16.dp
            if (announcing) {
                val msgBy = if (secret) alias else user?.name ?: user?.email ?: "Unknown User"
                val msgU = if (secret) "????" else user?.uid
                val msgP = if (secret) "????" else user?.photo ?: ""
                Dialog(
                    onDismissRequest = {
                        announcing = !announcing
                        Toast.makeText(
                            context,
                            "Secret Alias Activated",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                ) {
                    Column(
                        modifier = modifier
                            .fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {

                        TextField(
                            value = announceTitle,
                            label = { Text("Announcement Title") },
                            onValueChange = {
                                announceTitle = it
                            }
                        )
                        Spacer(modifier.fillMaxHeight(0.025f))
                        TextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(0.4f),
                            label = { Text("Message") },
                            value = announceText,
                            onValueChange = {
                                announceText = it
                            },
                        )
                        Spacer(modifier.fillMaxHeight(0.025f))
                        TextField(
                            label = { Text("Signature") },
                            value = announceSig,
                            onValueChange = {
                                announceSig = it
                            },
                            trailingIcon = {
                                IconButton(onClick =
                                {
                                    announcing = !announcing
                                    Toast.makeText(
                                        context,
                                        "New Announcement message posted by $msgBy",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    onEvent(
                                        ChatEvent.HooMkAnnouncement(
                                            ChatMessage(
                                                id = Random.nextLong(),
                                                title = announceTitle,
                                                msg = announceText,
                                                signature = announceSig,
                                                sender = msgBy,
                                                senderUid = msgU,
                                                senderPic = msgP,
                                                isAnnouncement = true,
                                                isPermanent = true,
                                                isPinned = true,
                                                isSecret = secret,
                                                isUnread = true,
                                                timestamp = formatDT(),
                                                timestampAbs = System.currentTimeMillis(),
                                            )
                                        )
                                    )
                                }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Anchor,
                                        contentDescription = "Announce"
                                    )
                                }
                            }
                        )
                    }
                }
            }
            else if (replying) {
                val replyToMsg = state.replyToMsg
                val replyTo = state.replyTo
                val msgBy = if (secret) alias else user?.name ?: user?.email ?: "Secret User"
                val msgU = if (secret) "????" else user?.uid
                val msgP = if (secret) "????" else user?.photo ?: ""
                Dialog(
                    onDismissRequest = {
                        replying = !replying
                        Toast.makeText(
                            context,
                            "Secret Alias Activated",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                ) {
                    Column(
                        modifier = modifier
                            .fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {

                        TextField(
                            value = replyTitle,
                            label = { Text("Reply Title") },
                            placeholder = { Text("Add a title...") },
                            onValueChange = {
                                replyTitle = it
                            }
                        )
                        Spacer(modifier.fillMaxHeight(0.025f))
                        TextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(0.4f),
                            label = { Text("Message") },
                            placeholder = { Text("Message body...") },
                            value = replyText,
                            onValueChange = {
                                replyText = it
                            },
                        )
                        Spacer(modifier.fillMaxHeight(0.025f))
                        TextField(
                            label = { Text("Signature") },
                            placeholder = { Text("Add a signature...") },
                            value = replySig,
                            onValueChange = {
                                replySig = it
                            },
                            trailingIcon = {
                                IconButton(onClick =
                                {
                                    replyToMsg?.let {
                                        replying = !replying
                                        Toast.makeText(
                                            context,
                                            "Replied to ${replyTo?.name}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        onEvent(
                                            ChatEvent.Reply(
                                                ChatMessage(
                                                    id = Random.nextLong(10000 * 10000),
                                                    title = replyTitle,
                                                    msg = replyText,
                                                    extraTxt = "(Private to ${replyTo?.name?: replyTo?.email?: "Unknown"})",
                                                    replyTxt = "'${replyToMsg.sender} on " +
                                                            "${replyToMsg.timestamp} said - " +
                                                            "${replyToMsg.msg}'",
                                                    signature = replySig,
                                                    sender = msgBy,
                                                    senderUid = msgU,
                                                    senderPic = msgP,
                                                    receiver = replyTo?.name ?: "Secret User",
                                                    receiverUid = replyTo?.uid ?: "????",
                                                    receiverPic = replyTo?.photo ?: "????",
                                                    readBy = listOf(msgBy),
                                                    isPrivate = replyToMsg.isPrivate || roomNo == 2,
                                                    isPermanent = true,
                                                    isReply = true,
                                                    isSecret = secret,
                                                    isUnread = true,
                                                    sent = true,
                                                    timestamp = formatDT(),
                                                    timestampAbs = System.currentTimeMillis(),
                                                ),
                                                replyToMsg
                                            )
                                        )
                                    }
                                }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Anchor,
                                        contentDescription = "Announce"
                                    )
                                }
                            }
                        )
                    }
                }
            }
            if (dialog) {
                Dialog(
                    onDismissRequest = {
                        dialog = !dialog
                        Toast.makeText(
                            context,
                            "Secret Alias Activated",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                ) {
                    TextField(
                        label = { Text("Change Alias: ") },
                        value = alias,
                        onValueChange = {
                            alias = it
                        },
                        trailingIcon = {
                            IconButton(onClick =
                            {
                                dialog = !dialog
                                Toast.makeText(
                                    context,
                                    "New Secret Alias: $alias",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Opacity,
                                    contentDescription = "Secret"
                                )
                            }
                        }
                    )
                }
            }
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Spacer(Modifier.height(12.5.dp))
                if (log.messages.isEmpty()) {
                    Text("No messages")
                }
                if (chatLog.isEmpty()) {
                    Text("Chat log is empty.")
                }
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                ) {
                    itemsIndexed(log.messages) { i, msg ->
                        Spacer(Modifier.height(7.5.dp))
                        ChatMessageItem(
                            msg = msg,
                            sender = msg.sender,
                            pic = msg.senderPic,
                            color = if (msg.sender == me) Iron else DarkBlueGray,
                            bgColor = if (msg.sender == me) BlueDiamond else GrayGoose,
                            modifier = modifier
                                //.align(if (msg.sender == me) Alignment.End else Alignment.Start)
                                .fillMaxWidth(0.8f),
                            user = state.user,
                            profile = log.messageMap[msg],
                            onBanClick = { u, b ->
                                if (b) onEvent(ChatEvent.Unban(u))
                                else onEvent(ChatEvent.Ban(u))
                            },
                            onBlockClick = { u, b ->
                                if (b) onEvent(ChatEvent.UnblockUser(u))
                                else onEvent(ChatEvent.BlockUser(u))
                            },
                            onEditSave = { m, new ->
                                onEvent(ChatEvent.EditMsg(m, new))
                            },
                            onDeleteClick = { m, b ->
                                if (!b) onEvent(ChatEvent.DeleteMsg(m))
                                else onEvent(ChatEvent.RestoreMsg(m))
                            },
                            onDeletePermClick = { m ->
                                scope.launch {
                                    onEvent(ChatEvent.DeleteMsgPerm(m))
                                    val result = scaffoldState.snackbarHostState.showSnackbar(
                                        message = "Deleted Message",
                                        actionLabel = "Undo"
                                    )
                                    if (result == SnackbarResult.ActionPerformed) {
                                        onEvent(ChatEvent.UndoDelete)
                                    }
                                }
                            },
                            onDemoteMessage = { m ->
                                onEvent(ChatEvent.DemoteMessage(m))
                            },
                            onDemoteUser = { ur ->
                                onEvent(ChatEvent.DemoteUser(ur))
                            },
                            onDisableClick = { u, b ->
                                onEvent(ChatEvent.DisableUser(u, !b))
//                                if (b) onEvent(ChatEvent.UnDisable(u))
//                                else onEvent(ChatEvent.Disable(u))
                            },
                            onFlagClick = { m ->
                                onEvent(ChatEvent.FlagMsg(m))
                            },
                            onHideClick = { m ->
                                onEvent(ChatEvent.HideMsg(m))
                            },
                            onMuteClick = { u, b ->
                                onEvent(ChatEvent.Mute(u, !b))
                            },
                            onPinClick = { m ->
                                onEvent(ChatEvent.PinMsg(m))
                            },
                            onPM = { to, title, body, sig ->
                                user?.let {
                                    onEvent(
                                        ChatEvent.SendPM(
                                            PrivateMessage(
                                                id = Random.nextLong(10000 * 10000),
                                                title = title,
                                                body = body,
                                                signature = sig,
                                                extraTxt = "(Private to ${to.name?:to.email?:"Unknown User"})",
                                                sender = user,
                                                recipient = to
                                            ),
                                            to
                                        )
                                    )
                                }
                            },
                            onPromoteMessage = { message, timer ->
                                onEvent(ChatEvent.PromoteMessage(message, timer))
                            },
                            onPromoteUser = { ur ->
                                if (ur == user)
                                    onEvent(ChatEvent.SelfPromote(ur))
                                else
                                    onEvent(ChatEvent.PromoteUser(ur))
                            },
                            onPurgeUser = { ur ->
                                if (ur != user) {
                                    onEvent(ChatEvent.Purge(ur.uid))
                                    // Add optional method to delete all user messages in the future
                                }
                            },
                            onReplyClick = { message, sender ->
                                replying = !replying
                                onEvent(ChatEvent.StartReply(message, sender))
                            },
                            onReportClick = { m, ur, b ->
                                onEvent(ChatEvent.ReportMsg(m, ur, !b))
                            },
                            onRestrictClick = {m, u, b ->
                                onEvent(ChatEvent.RestrictUser(m, u, !b))
                            },
                            onSilenceClick = { u, b ->
                                onEvent(ChatEvent.SilenceUser(u, !b))
                            },
                            onUnFlagClick = { m ->
                                onEvent(ChatEvent.UnFlagMsg(m))
                            },
                            onUnHideClick = { m ->
                                onEvent(ChatEvent.UnHideMsg(m))
                            },
                            onUnpinClick = { m ->
                                onEvent(ChatEvent.UnpinMsg(m))
                            },
                            onWarnClick = { m, ur, b ->
                                onEvent(ChatEvent.Warn(m, ur, !b))
                            }
                        )
                        Spacer(Modifier.height(7.5.dp))
                        Row(
                            modifier = Modifier
                                .align(Alignment.End)
                        )
                        {
                            if (msg.isDeleted) {
                                Icon(
                                    modifier = modifier
                                        .scale(0.5f),
                                    imageVector = Icons.Default.DeleteOutline,
                                    contentDescription = null
                                )
                            }
                            if (msg.isEdited) {
                                Icon(
                                    modifier = modifier
                                        .scale(0.5f),
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = null
                                )
                            }
                            if (msg.isFlagged) {
                                Icon(
                                    modifier = modifier
                                        .scale(0.5f),
                                    imageVector = Icons.Default.Flag,
                                    contentDescription = null
                                )
                            }
                            if (msg.isHidden) {
                                Icon(
                                    modifier = modifier
                                        .scale(0.5f),
                                    imageVector = Icons.Default.HideSource,
                                    contentDescription = null
                                )
                            }
                            if (msg.isPinned) {
                                Icon(
                                    modifier = modifier
                                        .scale(0.5f),
                                    imageVector = Icons.Default.PushPin,
                                    contentDescription = null
                                )
                            }
                        }
                    }
                }
                Spacer(Modifier.height(37.5.dp))
                var textSize by remember { mutableStateOf(Size(125f, 1000f)) }
                val pmTo: String = (log.selectedUser?.name?: log.selectedUser?.email?:"????").apply {
                    substring(0, min(10, this.length))
                }
                val messageLabel: String = when(roomNo) {
                    1 -> {
                        "Send Message"
                    }
                    2 -> {
                        "Send Private Message to $pmTo"
                    }
                    3 -> {
                        "Broadcast Announcement"
                    }
                    else -> {""}
                }
                val secretLabel = if (secret) " [Secret]" else ""
                TextField(
//            modifier = Modifier
//                .align(Alignment.BottomCenter),
                    enabled = user?.isWarned != true && user?.isRestricted != true
                            && user?.isMuted != true && user?.isSilenced != true,
                    readOnly = false,
                    value = state.msg,
                    leadingIcon = {
                        IconButton(onClick = {
                            if (secret) onEvent(ChatEvent.SendMsgSecret(state.msg, alias))
                            else onEvent(ChatEvent.SendMsg(state.msg))
                        }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ScheduleSend,
                                contentDescription = "dropdown menu icon",
                            )
                        }
                    },
                    label = { Text(messageLabel + secretLabel) },
                    placeholder = {Text (messageLabel + secretLabel) },
                    trailingIcon = {
                        IconButton(onClick = {
                            val sendTo = log.selectedUser?.name?: log.selectedUser?.email?: "Unknown"
                            val valid = log.selectedUser != null
                            if (secret && private) {
                                if (!valid) {
                                    Toast.makeText(
                                        context,
                                        "Cannot send message : Select a user",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                else {
                                    onEvent(
                                        ChatEvent.SendPM(
                                            PrivateMessage(
                                                id = Random.nextLong(10000 * 10000),
                                                title = "(Private to ${sendTo})",
                                                body = state.msg,
                                                signature = "~ $alias",
                                                extraTxt = "~ $alias",
                                                sender = FireUser(name = alias),
                                                recipient = log.selectedUser,
                                                secret = true
                                            ),
                                            log.selectedUser
                                        )
                                    )
                                }
                            }
                            else if (secret) {
                                onEvent(ChatEvent.SendMsgSecret(state.msg, alias))
                            }
                            else if (private && user!= null) {
                                onEvent(
                                    ChatEvent.SendPM(
                                        PrivateMessage(
                                            id = Random.nextLong(10000 * 10000),
                                            title = "(Private to ${sendTo})",
                                            body = state.msg,
                                            signature = if (signed) sig else "",
                                            extraTxt = user.signature?: "",
                                            sender = user,
                                            recipient = log.selectedUser
                                        ),
                                        log.selectedUser
                                    )
                                )
                            }
                            else {
                                onEvent(ChatEvent.SendMsg(
                                    state.msg,
                                    sig
                                ))
                            }
                        }
                        ) {
                            Icon(
                                imageVector = if (secret) Icons.Filled.SendAndArchive else Icons.Filled.Send,
                                contentDescription = "Send Message",

                                )
                        }
                    },
                    textStyle = MaterialTheme.typography.h5,
                    onValueChange = { onEvent(ChatEvent.ChangeMsgText(it)) },
                    maxLines = 1,
                    modifier = Modifier
                        .fillMaxWidth()
                        .onGloballyPositioned { coordinates ->
                            textSize = coordinates.size.toSize()
                        }
                        .wrapContentSize(Alignment.BottomCenter)
                        .background(
                            color = MidnightBlue,
                            shape = RoundedCornerShape(20.dp)
                        )
                )
            }
        }
    }
}

fun formatDT(dt: LocalDateTime = LocalDateTime.now()): String = run {
    DateTimeFormatter
        .ofPattern("EEEE, MMM dd yyyy hh:mm a")
        .format(dt)
}?: "00:00"