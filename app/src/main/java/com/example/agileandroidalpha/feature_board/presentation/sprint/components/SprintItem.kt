package com.example.agileandroidalpha.feature_board.presentation.sprint.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.CommentBank
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Pending
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils
import coil.compose.AsyncImage
import com.example.agileandroidalpha.R
import com.example.agileandroidalpha.feature_board.presentation.sprint.msToDays
import com.example.agileandroidalpha.feature_board.presentation.tasks.components.calcColor
import com.example.agileandroidalpha.firebase.firestore.model.FireUser
import com.example.agileandroidalpha.firebase.firestore.model.Sprint
import com.example.agileandroidalpha.firebase.firestore.model.Story
import com.example.agileandroidalpha.firebase.firestore.model.SubTask
import com.example.agileandroidalpha.ui.theme.DeepSeaBlue
import com.example.agileandroidalpha.ui.theme.FieryRose
import com.example.agileandroidalpha.ui.theme.LapisBlue
import com.example.agileandroidalpha.ui.theme.Lotus
import com.example.agileandroidalpha.ui.theme.MetallicBronze
import com.example.agileandroidalpha.ui.theme.PastelLilac
import com.example.agileandroidalpha.ui.theme.PurpleNavy
import com.example.agileandroidalpha.ui.theme.Sandstone
import com.example.agileandroidalpha.ui.theme.TiffanyBlue
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.roundToLong
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime

@Composable
fun SprintItem(
    sprint: Sprint,
    stories: List<Story>,
    subtasks: List<SubTask>?,
    userList: List<FireUser>?,
    user: FireUser?,
    modifier: Modifier = Modifier,
    cRad: Dp = 10.dp,
    cutoff: Dp = 30.dp,
    collapsed: Boolean,
    collapsed2: Boolean,
    onCheckboxClick: () -> Unit,
    onCheckStoryClick: (Story, Boolean, Long) -> Unit,
    onCompleteClick: (Sprint, Boolean) -> Unit,
    onDeleteClick: (Sprint, List<Story>, List<SubTask>?, FireUser?) -> Unit,
    onDeleteStory: (Story) -> Unit,
    onDragStory: (Story, Sprint, Boolean) -> Unit,
    //onEditSprint: (Sprint, List<Story>, List<SubTask>?, List<FireUser>?) -> Unit,
    onEditStory: (Story) -> Unit,
    onExpandClick: (Boolean) -> Unit,
    onExpandComments: (Boolean) -> Unit,
    onOpenDialog: (Sprint) -> Unit,
    onOpenStoryDialog: (Story) -> Unit,
//    onReview: () -> Unit,
//    onApprove: () -> Unit,
//    onArchive: (Sprint, FireUser?) -> Unit,
    onMoveStory: (Story, Sprint, Long, Int) -> Unit,
    onRevoke: () -> Unit,
    onExtendClick: () -> Unit
//    onMoveStoryClick: (Story, Long) -> Unit,
) {

    val totalPoints = stories.sumOf { t -> t.points }
    val remPoints = stories.filter {t -> !t.done} .sumOf { t -> t.points }
    val context = LocalContext.current
    val pro = calc(sprint, stories)

    fun expiredColor () = run {
        if(!sprint.expired) {
            Color(sprint.color)
        }
        else {
            Sandstone
        }
    }

    @OptIn(ExperimentalTime::class)
    fun formatDateDisplay(dt: Long?): String? = run {
        DateTimeFormatter
            .ofPattern("MMM dd yyyy")
            .format(LocalDate.ofEpochDay(msToDays(dt?:0)))
    }
    @ExperimentalTime
    fun msToDays(tym: Long): Long {
        return Duration.convert(tym + 0.0, DurationUnit.MILLISECONDS, DurationUnit.DAYS).roundToLong()
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
                    color = expiredColor(),
                    size = size,
                    cornerRadius = CornerRadius(cRad.toPx())
                )
                drawRoundRect(
                    color = expiredColor(),
                    size = size,
                    cornerRadius = CornerRadius(cRad.toPx())
                )
                drawRoundRect(
                    color = Color(
                        ColorUtils.blendARGB(sprint.color, 0x000000, 0.2f)
                    ),
                    topLeft = Offset(size.width - cutoff.toPx(), -100f),
                    size = Size(cutoff.toPx() + 100f, cutoff.toPx() + 100f),
                    cornerRadius = CornerRadius(cRad.toPx())
                )
                drawRoundRect(
                    color = Color(
                        ColorUtils.blendARGB(TiffanyBlue.toArgb(), PastelLilac.toArgb(), 0.5f)
                    ),
                    topLeft = Offset(size.width / 2.25f, 15f),
                    size = Size(175f, 125f),
                    cornerRadius = CornerRadius(cRad.toPx())
                )
            }
        }
        IconButton(
            onClick = { onOpenDialog(sprint) },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset((-60).dp)
                .padding(end = 10.dp),
        ) {
            Icon(
                imageVector = Icons.Default.Pending,
                contentDescription = "Move Backlog Position",
                tint = MaterialTheme.colors.onSurface
            )
        }
        Text(
            text = "${sprint.totalPoints}",
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = 10.dp),
            fontWeight = FontWeight.W900,
            style = MaterialTheme.typography.body1,
            color = MaterialTheme.colors.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Clip
        )
        Text(
            text = sprint.status?: "Not Started",
            textDecoration = if (sprint.completed) TextDecoration.LineThrough
            else TextDecoration.None,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset((-30).dp, 12.dp)
                .padding(end = 30.dp),
            style = MaterialTheme.typography.body1,
            color = MaterialTheme.colors.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Clip
        )
        IconButton(
            onClick = onCheckboxClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset((-10).dp)
                .padding(end = 10.dp),
        ) {
            Icon(
                imageVector = if (sprint.completed) Icons.Default.CheckBox
                else Icons.Default.CheckBoxOutlineBlank,
                contentDescription = "Toggle Priority",
                tint = MaterialTheme.colors.onSurface
            )
        }
        CircularProgressIndicator(
            progress = pro.second,
            modifier = Modifier
                .offset(5.dp, 5.dp),
            color = calcColor(pro.second),
            backgroundColor = FieryRose,
            strokeCap = StrokeCap.Round
        )
        IconButton(
            onClick = { /*TODO*/ },
            modifier = Modifier.align(Alignment.TopStart)
        ) {
            Image(
                painter = painterResource(R.drawable.id_bozo),
                contentDescription = "Icon",
                modifier = Modifier
                    .scale(2.0F)
                    .clip(CircleShape),
                alignment = Alignment.TopCenter
            )
        }
        Row(
            modifier = Modifier
                .offset(y = 100.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (sprint.started) {
                Image(
                    painterResource(R.drawable.ic_signal),
                    contentDescription = "Frozen",
                    modifier = Modifier
                        .offset(x = 10.dp, y = 30.dp)
                        .size(30.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    painterResource(R.drawable.ic_ice),
                    contentDescription = "Frozen",
                    modifier = Modifier
                        .offset(x = 10.dp, y = 30.dp)
                        .size(30.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }
            if (sprint.paused) {
                Image(
                    painterResource(R.drawable.ic_para),
                    contentDescription = "Frozen",
                    modifier = Modifier
                        .offset(x = 25.dp, y = 30.dp)
                        .size(30.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    painterResource(R.drawable.ic_shock),
                    contentDescription = "Frozen",
                    modifier = Modifier
                        .offset(x = 25.dp, y = 30.dp)
                        .size(30.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }
            if (sprint.cloned) {
                Image(
                    painterResource(R.drawable.ic_twins),
                    contentDescription = "Frozen",
                    modifier = Modifier
                        .offset(x = 40.dp, y = 30.dp)
                        .size(30.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    painterResource(R.drawable.ic_gol),
                    contentDescription = "Frozen",
                    modifier = Modifier
                        .offset(x = 40.dp, y = 30.dp)
                        .size(30.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }
            if (sprint.completed) {
                Image(
                    painterResource(R.drawable.ic_pipe),
                    contentDescription = "Frozen",
                    modifier = Modifier
                        .offset(x = 55.dp, y = 30.dp)
                        .size(30.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    painterResource(R.drawable.ic_unknown),
                    contentDescription = "Question",
                    modifier = Modifier
                        .offset(x = 55.dp, y = 30.dp)
                        .size(30.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }
        }
//        if (user != null) {
//            FilledTonalButton(
//                onClick = onReview,
//                enabled = sprint.completed && !sprint.isApproved && (user.isPowerUser || user.isVerified),
//                modifier = Modifier
//                    .align(Alignment.TopEnd)
//                    .offset(y = (70).dp)
//                    .padding(end = 2.dp),
//            ) {
//                val reText = if (sprint.isReviewed) "Cancel Review" else "Mark Reviewed"
//                Text(reText)
//                Icon(
//                    imageVector = Icons.Default.RateReview,
//                    contentDescription = "Reviewing",
//                    tint = MaterialTheme.colors.onSurface
//                )
//            }
//            FilledTonalButton(
//                onClick = onApprove,
//                enabled = sprint.isReviewed && user.isAdmin || (user.isPowerUser && user.isVerified),
//                modifier = Modifier
//                    .align(Alignment.TopEnd)
//                    .offset(y = (120).dp)
//                    .padding(end = 2.dp),
//            ) {
//                val appText = if (sprint.isApproved) "Revoke Approval" else "Approve Sprint"
//                Text(appText)
//                Icon(
//                    imageVector = Icons.Default.Approval,
//                    contentDescription = "Approval",
//                    tint = MaterialTheme.colors.onSurface
//                )
//            }
//            FilledTonalButton(
//                onClick = { onArchive(sprint, user) },
//                enabled = sprint.isApproved && user.isAdmin || (user.isPowerUser && user.isVerified),
//                modifier = Modifier
//                    .align(Alignment.TopEnd)
//                    .offset(y = (170).dp)
//                    .padding(end = 2.dp),
//            ) {
//                Text("Archive Sprint")
//                Icon(
//                    imageVector = Icons.Default.Archive,
//                    contentDescription = "Archival",
//                    tint = MaterialTheme.colors.onSurface
//                )
//            }
//        }

        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(top = 22.dp, end = 32.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(4.dp)
                    .padding(top = 3.dp, end = 0.dp)
                    .align(Alignment.CenterHorizontally),
                horizontalArrangement = Arrangement.Center
            ) {
                val cloneText = " [CLONE] "
                Text(
                    text = cloneText + sprint.title.orEmpty(),
                    textDecoration = if (sprint.completed) TextDecoration.LineThrough
                    else TextDecoration.None,
                    style = MaterialTheme.typography.h6,
                    color = DeepSeaBlue,//MaterialTheme.colors.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Row(
                modifier = Modifier
                    .padding(2.dp)
                    .padding(end = 9.dp)
                    .align(Alignment.CenterHorizontally),
                horizontalArrangement = Arrangement.Center
            ) {
                val durationText = if (sprint.started ) "from ${formatDate(sprint.startDate!!)} to ${formatDate(sprint.endDate!!)}"
                                else "Duration : ${sprint.duration}"
                Text(
                    text = "Sprint ${sprint.id} ",
                    textDecoration = if (sprint.completed) TextDecoration.LineThrough
                    else TextDecoration.None,
                    style = MaterialTheme.typography.body2,
                    color = LapisBlue,
                    maxLines = 2,
                    overflow = TextOverflow.Visible
                )
            }
            Spacer(modifier = Modifier.height(11.dp))
            Row(
                modifier = Modifier
                    .padding(2.dp)
                    .padding(end = 9.dp)
                    .offset(y = (-10).dp)
                    .align(Alignment.CenterHorizontally),
                horizontalArrangement = Arrangement.Center
            ) {
                val startedText =  if (sprint.started || sprint.expired) " of ${sprint.duration}" else ""
                val startText2 = if (sprint.started || sprint.expired) "Remaining" else "(Not Started)"
                Text(
                    text = "${sprint.countdown}$startedText Days $startText2",
                    textDecoration = if (sprint.completed) TextDecoration.LineThrough
                    else TextDecoration.None,
                    style = MaterialTheme.typography.caption,
                    color = Lotus,
                    maxLines = 2,
                    overflow = TextOverflow.Visible
                )
            }
            if (!userList.isNullOrEmpty()) {
                LazyRow(
                    modifier = Modifier,
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    itemsIndexed(userList) { index, item ->
                        AsyncImage(
                            model = item.photo,
                            contentDescription = "Mini profile picture",
                            modifier = Modifier
                                .offset(x = (-15).dp, y = (-10).dp)
                                .size(30.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop,
                            error = painterResource(id = R.drawable.ic_talk)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(11.dp))
            }
            androidx.compose.material3.Text(
                modifier = Modifier.offset(x = 200.dp),
                text = "Start Date: ${formatDateDisplay(sprint.startDate)}" +
                        "\nEnd Date: ${formatDateDisplay(sprint.endDate)}",
                color = MetallicBronze,
                fontWeight = FontWeight.Bold
            )
            Row(

            ) {
                Spacer(modifier = Modifier.height(11.dp))
                Icon(
                    Icons.Default.run {
                        if (collapsed)
                            KeyboardArrowDown
                        else
                            KeyboardArrowUp
                    },
                    contentDescription = "",
                    tint = Color.LightGray,
                    modifier = Modifier
                        .clickable {
                            onExpandClick(!collapsed)
                        }
                )
            }
            Divider()
            if (stories.isNotEmpty()) {
                stories.forEachIndexed() { i, sto ->
                    if(!collapsed) {
                        StoryItem(
                            idx = i,
                            story = sto,
                            status = sto.status,
                            isDone = sto.done,
                            subtasks = subtasks?.filter { sb -> sb.storyId == sto.id }.orEmpty(),
                            onCheckboxClick = onCheckStoryClick,
//                            onClickedChange = {},
                            onEditClick = onEditStory,
                            onDeleteClick = onDeleteStory,
                            onMoveTo = { st, sp ->
                                onMoveStory(st, sp, sp.id!!, sp.backlogWt) } ,
                            onOpenStoryDialog = onOpenStoryDialog,
//                            onDraggedChange = onDragSubtask,
                            modifier = Modifier
                            .clickable {
                                onEditStory(sto)
                            }
                        )
                    }
                    // Gud place to insert StoryItem
                }
            } else {
                Text(
                    modifier = Modifier
                        .padding(end = 5.dp),
                    text = "No Stories"
                )
            }
            Spacer(modifier = Modifier.height(22.dp))
            if (!sprint.comments.isNullOrEmpty() && !sprint.signatures.isNullOrEmpty()) {
                val sz = sprint.signatures!!.lastIndex
                sprint.comments!!.forEachIndexed() { i, comm ->
                    if(!collapsed2) {
                        Text(
                            modifier = Modifier
                                //.align(Alignment.BottomCenter)
                                .clickable {

                                }
                                .padding(all = 12.5.dp),
                            text = comm,
                            color = PurpleNavy
                        )
                        Text(
                            modifier = Modifier,
                                //align(Alignment.BottomCenter)
                                //.offset(x = 25.dp, y = 15.dp),
                            text = " - " + if (i <= sz) sprint.signatures!![i] else "No user found",
                            fontStyle = FontStyle.Italic
                        )
                    }

                }
            } else {
                Text(
                    modifier = Modifier
                        .padding(end = 5.dp),
                    text = "No Comments"
                )
            }
        }
        if(collapsed || collapsed2) {
            Text(
                modifier = Modifier
                    .align(Alignment.BottomCenter),
                text = "${totalPoints - remPoints} / $totalPoints (${pro.first * 100f}%) story points satisfied \n" +
                        "${pro.second * 100f}% stories completed",
                color = calcColor(pro.second),
            )
        }
        Icon(
            Icons.Default.run {
                if (collapsed2)
                    CommentBank
                else
                    Comment
            },
            contentDescription = "",
            tint = Color.LightGray,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .clickable {
                    onExpandComments(!collapsed2)
                }
        )


        IconButton(
            onClick = { onDeleteClick(sprint, stories, subtasks, user) },
            modifier = Modifier.align(Alignment.BottomEnd)
        ) {
            Icon(
                imageVector = Icons.Default.DeleteSweep,
                contentDescription = "Delete Task",
                tint = MaterialTheme.colors.onSurface
            )
        }
        Spacer(modifier = Modifier.height(30.dp))
    }

}

fun calc(sp: Sprint, sts: List<Story>): Triple<Float, Float, Float> {
    if (sts.isEmpty()) return Triple(0f, 0f, sp.target)
    var x = 0L
    var y = 0L
    var z = 0f
    val sz = sts.size
    sts.forEachIndexed { i, st ->
        if (st.done) {
            z++
        }
        y += st.points
        if (st.done) {
            x += st.points
        }
    }
    sp.totalPoints = y
    sp.remPoints = sp.totalPoints - x
    if(y < 1) y = 1
    return Triple (x.div(y) * 1f, z.div(sz), sp.target)
}

fun formatDate(date: Long): String {
    val dt = LocalDate.ofEpochDay(date)
    return "${dt.month}-${dt.dayOfMonth}-${dt.year}"
}