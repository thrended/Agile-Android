//package com.example.agileandroidalpha.feature_board.presentation.tasks.components
//
//import androidx.compose.foundation.Canvas
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.offset
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.material.CircularProgressIndicator
//import androidx.compose.material.Divider
//import androidx.compose.material.Icon
//import androidx.compose.material.IconButton
//import androidx.compose.material.LinearProgressIndicator
//import androidx.compose.material.MaterialTheme
//import androidx.compose.material.Text
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.AddCard
//import androidx.compose.material.icons.filled.CheckBox
//import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
//import androidx.compose.material.icons.filled.DeleteSweep
//import androidx.compose.material.icons.filled.DonutLarge
//import androidx.compose.material.icons.filled.DonutSmall
//import androidx.compose.material.icons.filled.ElectricBolt
//import androidx.compose.material.icons.filled.Emergency
//import androidx.compose.material.icons.filled.KeyboardArrowDown
//import androidx.compose.material.icons.filled.KeyboardArrowUp
//import androidx.compose.material.icons.filled.KeyboardDoubleArrowDown
//import androidx.compose.material.icons.filled.KeyboardDoubleArrowUp
//import androidx.compose.material.icons.filled.LowPriority
//import androidx.compose.material.icons.filled.North
//import androidx.compose.material.icons.filled.PriorityHigh
//import androidx.compose.material.icons.filled.Thunderstorm
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.geometry.CornerRadius
//import androidx.compose.ui.geometry.Offset
//import androidx.compose.ui.geometry.ID
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.Path
//import androidx.compose.ui.graphics.StrokeCap
//import androidx.compose.ui.graphics.drawscope.clipPath
//import androidx.compose.ui.graphics.toArgb
//import androidx.compose.ui.graphics.vector.ImageVector
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextDecoration
//import androidx.compose.ui.text.style.TextOverflow
//import androidx.compose.ui.unit.Dp
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.core.graphics.ColorUtils
//import coil.compose.AsyncImage
//import com.example.agileandroidalpha.Priority
//import com.example.agileandroidalpha.R
//import com.example.agileandroidalpha.feature_board.domain.model.Subtask
//import com.example.agileandroidalpha.feature_board.domain.model.Task
//import com.example.agileandroidalpha.ui.theme.Cobalt
//import com.example.agileandroidalpha.ui.theme.Emerald
//import com.example.agileandroidalpha.ui.theme.FieryRose
//import com.example.agileandroidalpha.ui.theme.ForbiddenGud
//import com.example.agileandroidalpha.ui.theme.Jade
//import com.example.agileandroidalpha.ui.theme.LBGold
//import com.example.agileandroidalpha.ui.theme.PastelLilac
//import com.example.agileandroidalpha.ui.theme.TiffanyBlue
//import com.example.agileandroidalpha.ui.theme.ValentineRed
//
//@Composable
//fun TaskItemBACKUP(
//    task: Task,
//    subtasks: List<Subtask>,
//    modifier: Modifier = Modifier,
//    cRad: Dp = 10.dp,
//    cutoff: Dp = 30.dp,
//    collapsed: Boolean,
//    onAddSubClick: () -> Unit,
//    onCheckboxClick: () -> Unit,
//    onCheckSubtask: (Subtask, Boolean) -> Unit,
//    onDeleteClick: () -> Unit,
//    onDeleteSubtask: (Subtask) -> Unit,
//    onDragSubtask: (Subtask, String, Boolean) -> Unit,
//    onEditSubtask: (Subtask, Long) -> Unit,
//    onExpandClick: (Boolean) -> Unit,
//) {
//    Box(
//        modifier = modifier
//    ){
//        Canvas(modifier = Modifier.matchParentSize()) {
//            val clipPath = Path().apply {
//                lineTo(size.width - cutoff.toPx(), 0f)
//                lineTo(size.width, cutoff.toPx())
//                lineTo(size.width, size.height)
//                lineTo(0f, size.height)
//                close()
//            }
//
//            clipPath(clipPath) {
//                drawRoundRect(
//                    color = Color(task.color),
//                    size = size,
//                    cornerRadius = CornerRadius(cRad.toPx())
//                )
//                drawRoundRect(
//                    color = Color(
//                        ColorUtils.blendARGB(task.color, 0x000000, 0.2f)
//                    ),
//                    topLeft = Offset(size.width - cutoff.toPx(), -100f),
//                    size = ID(cutoff.toPx() + 100f, cutoff.toPx() + 100f),
//                    cornerRadius = CornerRadius(cRad.toPx())
//                )
//                drawRoundRect(
//                    color = Color(
//                        ColorUtils.blendARGB(TiffanyBlue.toArgb(), PastelLilac.toArgb(), 0.5f)
//                    ),
//                    topLeft = Offset(size.width / 2.25f, 15f),
//                    size = ID(125f, 75f),
//                    cornerRadius = CornerRadius(cRad.toPx())
//                )
//            }
//        }
//        IconButton(
//            onClick = { /*TODO*/ },
//            modifier = Modifier.align(Alignment.TopStart)
//        ) {
//            Icon(
//                imageVector = iconPri(task.priority),
//                contentDescription = "Change Priority",
//                tint = MaterialTheme.colors.onSurface
//            )
//        }
//        Text(
//            text = "${task.points}",
//            modifier = Modifier
//                .align(Alignment.TopCenter)
//                .offset(y = 10.dp),
//            fontWeight = FontWeight.W900,
//            style = MaterialTheme.typography.body1,
//            color = MaterialTheme.colors.onSurface,
//            maxLines = 1,
//            overflow = TextOverflow.Clip
//        )
//        Text(
//            text = task.status,
//            textDecoration = if (task.done) TextDecoration.LineThrough
//            else TextDecoration.None,
//            modifier = Modifier
//                .align(Alignment.TopEnd)
//                .offset((-30).dp, 12.dp)
//                .padding(end = 30.dp),
//            style = MaterialTheme.typography.body1,
//            color = MaterialTheme.colors.onSurface,
//            maxLines = 1,
//            overflow = TextOverflow.Clip
//        )
//        IconButton(
//            onClick = onCheckboxClick,
//            modifier = Modifier
//                .align(Alignment.TopEnd)
//                .offset((-10).dp)
//                .padding(end = 10.dp),
//        ) {
//            Icon(
//                imageVector = if (task.done) Icons.Default.CheckBox
//                else Icons.Default.CheckBoxOutlineBlank,
//                contentDescription = "Toggle Priority",
//                tint = MaterialTheme.colors.onSurface
//            )
//        }
//        CircularProgressIndicator(
//            progress = calcProgress(subtasks),
//            modifier = Modifier
//                .offset(5.dp, 5.dp),
//            color = calcColor(calcProgress(subtasks)),
//            backgroundColor = FieryRose,
//            strokeCap = StrokeCap.Round
//        )
//        Column (
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(16.dp)
//                .padding(top = 22.dp, end = 32.dp)
//        ) {
//            Row (modifier = Modifier
//                .padding(4.dp)
//                .padding(top = 3.dp, end = 0.dp)
//                .align(Alignment.CenterHorizontally),
//                horizontalArrangement = Arrangement.Center
//            ) {
//                Text(
//                    text = if(task.cloned) "[CLONE] ${task.title}" else task.title,
//                    textDecoration = if (task.done) TextDecoration.LineThrough
//                    else TextDecoration.None,
//                    style = MaterialTheme.typography.h6,
//                    color = MaterialTheme.colors.onSurface,
//                    maxLines = 2,
//                    overflow = TextOverflow.Clip
//                )
//            }
//            Row (modifier = Modifier
//                .padding(2.dp)
//                .padding(top = 3.dp, end = 9.dp)
//                .align(Alignment.CenterHorizontally),
//                horizontalArrangement = Arrangement.Center
//            ) {
//                Text(
//                    text = task.content,
//                    textDecoration = if (task.done) TextDecoration.LineThrough
//                    else TextDecoration.None,
//                    style = MaterialTheme.typography.body1,
//                    color = MaterialTheme.colors.onSurface,
//                    maxLines = 2,
//                    overflow = TextOverflow.Ellipsis
//                )
//            }
//            Spacer(modifier = Modifier.height(11.dp))
//
//            LinearProgressIndicator(
//                progress = calcProgress(subtasks),
//                modifier = Modifier
//                    .fillMaxWidth(0.5f)
//                    .align(Alignment.CenterHorizontally),
//                color = calcColor(calcProgress(subtasks)),
//                backgroundColor = ForbiddenGud,
//                strokeCap = StrokeCap.Round
//            )
//            Row(
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically,
//                modifier = Modifier
//                    .clickable {
//                        //onExpandClick(!collapsed)
//                    }
//            ) {
//                Icon(
//                    Icons.Default.run {
//                        if (collapsed)
//                            KeyboardArrowDown
//                        else
//                            KeyboardArrowUp
//                    },
//                    contentDescription = "",
//                    tint = Color.LightGray,
//                    modifier = Modifier
//                        .clickable {
//                            onExpandClick(!collapsed)
//                        }
//                )
//
//                if(toLogo(task.status) > 0) {
//                    Image(
//                        painterResource(id = toLogo(task.status)),
//                        contentDescription = "Issue Type Logo",
//                        modifier = Modifier
//                            //.offset(x = (-15).dp, y = (-10).dp)
//                            .size(30.dp)
//                            .clip(CircleShape),
//                        contentScale = ContentScale.Crop
//                    )
//                }
//            }
//            Divider()
//            if (subtasks.isNotEmpty()) {
//                subtasks.forEachIndexed() { i, sub ->
//                    if(!collapsed) {
//                        SubtaskItem(
//                            i,
//                            sub,
//                            sub.done,
//                            onCheckboxClick = onCheckSubtask,
//                            onClickedChange = {},
//                            onDeleteClick = onDeleteSubtask,
//                            onDraggedChange = onDragSubtask,
//                            onEditClick = onEditSubtask
//                        )
//                    }
//                    // Gud place to insert SubtaskItem
//                }
//            } else {
//                Text(
//                    modifier = Modifier
//                        .padding(end = 5.dp),
//                    text = "No subtasks"
//                )
//            }
//            Spacer(modifier = Modifier.height(22.dp))
//        }
//        Divider()
//        IconButton(
//            onClick = onAddSubClick,
//            modifier = Modifier.align(Alignment.BottomStart)
//        ) {
//            Icon(
//                imageVector = Icons.Default.AddCard,
//                contentDescription = "Add Subtask",
//                tint = MaterialTheme.colors.onSurface
//            )
//        }
//        if(!task.assUri.isNullOrBlank()) {       // Assignee assignee online avatar
//            AsyncImage(
//                model = task.assUri,
//                contentDescription = "Mini profile picture",
//                modifier = Modifier
//                    .offset(x = (-15).dp, y = (-10).dp)
//                    .size(30.dp)
//                    .clip(CircleShape)
//                    .align(Alignment.BottomCenter),
//                contentScale = ContentScale.Crop,
//                error = painterResource(id = R.drawable.box_red)
//            )
//        }
//
//        else{
//            Text(
//                text = task.assignee?: "None",   // In lieu of user's avatar
//                modifier = Modifier.align(Alignment.BottomCenter),
//                style = MaterialTheme.typography.body2,
//                color = Cobalt,
//                maxLines = 1,
//                fontSize = 10.sp,
//                overflow = TextOverflow.Visible
//            )
//        }
//        if(!task.repUri.isNullOrBlank()) {       // Assignee assignee online avatar
//            AsyncImage(
//                model = task.repUri,
//                contentDescription = "Mini profile picture",
//                modifier = Modifier
//                    .offset(x = 15.dp, y = (-10).dp)
//                    .size(30.dp)
//                    .clip(CircleShape)
//                    .align(Alignment.BottomCenter),
//                contentScale = ContentScale.Crop,
//                error = painterResource(id = R.drawable.box_red)
//            )
//        }
//        else{
//            Text(
//                text = task.reporter?: "None",   // In lieu of user's avatar
//                modifier = Modifier.align(Alignment.BottomCenter),
//                style = MaterialTheme.typography.body2,
//                color = Cobalt,
//                maxLines = 1,
//                fontSize = 10.sp,
//                overflow = TextOverflow.Visible
//            )
//        }
//        IconButton(
//            onClick = onDeleteClick,
//            modifier = Modifier.align(Alignment.BottomEnd)
//        ) {
//            Icon(
//                imageVector = Icons.Default.DeleteSweep,
//                contentDescription = "Delete Task",
//                tint = MaterialTheme.colors.onSurface
//            )
//        }
//    }
//}
//
//fun iconPri(pri: Priority): ImageVector {
//    when(pri) {
//        Priority.Donut -> {
//            return Icons.Default.DonutLarge
//        }
//        Priority.None -> {
//            return Icons.Default.DonutSmall
//        }
//        Priority.Trivial -> { // Trivial
//            return Icons.Default.LowPriority
//        }
//        Priority.Lowest -> { // Lowest
//            return Icons.Default.KeyboardDoubleArrowDown
//        }
//        Priority.Low -> { // Low
//            return Icons.Default.KeyboardArrowDown
//        }
//        Priority.Medium -> { // Medium
//            return Icons.Default.North
//        }
//        Priority.High -> { // High
//            return Icons.Default.KeyboardArrowUp
//        }
//        Priority.Highest -> { // Highest
//            return Icons.Default.KeyboardDoubleArrowUp
//        }
//        Priority.Critical -> { // Critical
//            return Icons.Default.Emergency
//        }
//        Priority.Urgent -> {
//            return Icons.Default.PriorityHigh
//        }
//        Priority.Showstopper -> { // Showstopper
//            return Icons.Default.ElectricBolt
//        }
//        Priority.Catastrophic -> {
//            return Icons.Default.Thunderstorm
//        }
//    }
//}
//
//fun paintPri(pri: Priority): Int {
//    return when(pri.value) {
//        1 -> {
//            R.drawable.jz
//        }
//        8 -> {
//            R.drawable.deband
//        }
//        9 -> {
//            R.drawable.shifta
//        }
//        10 -> {
//            R.drawable.sd
//        }
//        else -> {
//            0
//        }
//    }
//}
//
//fun toLogo(typ: String): Int {
//    return when (typ) {
//        "Task" -> {
//            R.drawable.id_viridia
//        }
//        "Story" -> {
//            R.drawable.id_blue
//        }
//        "Subtask" -> {
//            R.drawable.box_green
//        }
//        "Impediment" -> {
//            R.drawable.ic_wall
//        }
//        "Bug" -> {
//            R.drawable.box_red
//        }
//        "Feature" -> {
//            R.drawable.ic_grab
//        }
//        "Initiative" -> {
//            R.drawable.ic_pipe
//        }
//        "Issue" -> {
//            R.drawable.ic_unknown
//        }
//        "Test" -> {
//            R.drawable.ic_ry
//        }
//        "Spike" -> {
//            R.drawable.ic_trap
//        }
//        "ChangeRequest" -> {
//            R.drawable.ic_swap
//        }
//        "Epic" -> {
//            R.drawable.ic_twins
//        }
//        else -> {
//            -1
//        }
//    }
//}
//
//fun calcProgress(subtasks: List<Subtask>): Float {
//    if (subtasks.isEmpty()) return 0f
//    var x = 0f
//    val size = subtasks.size
//    subtasks.forEachIndexed() { i, sub ->
//        if (sub.done) {
//            x++
//        } else if (sub.status == "In Progress") {
//            x += 0.33f
//        }
//    }
//    return x / size
//}
//
//fun calcColor(progress: Float): Color {
//    if (progress < 0.5f) {
//        return Color(
//            ColorUtils.blendARGB(
//                ValentineRed.toArgb(), LBGold.toArgb(), progress * 2
//            )
//        )
//    }
//    return Color(
//        ColorUtils.blendARGB(
//            Emerald.toArgb(), Jade.toArgb(), (progress - 0.5f) * 2
//        )
//    )
//}