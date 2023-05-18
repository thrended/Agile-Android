package com.example.agileandroidalpha.feature_board.presentation.sprint.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.Card
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.ColorUtils
import coil.compose.AsyncImage
import com.example.agileandroidalpha.Priority
import com.example.agileandroidalpha.R
import com.example.agileandroidalpha.feature_board.presentation.tasks.components.iconPri
import com.example.agileandroidalpha.feature_board.presentation.tasks.components.moveBox
import com.example.agileandroidalpha.firebase.firestore.model.Sprint
import com.example.agileandroidalpha.firebase.firestore.model.Story
import com.example.agileandroidalpha.firebase.firestore.model.SubTask
import com.example.agileandroidalpha.ui.theme.BrownBear
import com.example.agileandroidalpha.ui.theme.Cobalt
import com.example.agileandroidalpha.ui.theme.ColdFrontGreen
import com.example.agileandroidalpha.ui.theme.DarkGray
import com.example.agileandroidalpha.ui.theme.DarkSlate
import com.example.agileandroidalpha.ui.theme.Grape
import com.example.agileandroidalpha.ui.theme.LBGold
import com.example.agileandroidalpha.ui.theme.LightCyan
import com.example.agileandroidalpha.ui.theme.LightGreen
import com.example.agileandroidalpha.ui.theme.PaleYellow
import com.example.agileandroidalpha.ui.theme.PastelLilac
import com.example.agileandroidalpha.ui.theme.PurpleSage
import com.example.agileandroidalpha.ui.theme.Shapes
import com.example.agileandroidalpha.ui.theme.TiffanyBlue
import com.example.agileandroidalpha.ui.theme.TropicalViolet
import kotlin.math.roundToInt

@Composable
fun StoryItem(
    idx: Int,
    story: Story,
    status: String,
    isDone: Boolean,
    subtasks: List<SubTask>,
    onCheckboxClick: (Story, Boolean, Long) -> Unit,
    onDeleteClick: (Story) -> Unit,
    //onDraggedChange: (Story, Sprint, Long) -> Unit,
    onEditClick: (Story) -> Unit,
    onOpenStoryDialog: (Story) -> Unit,
    onMoveTo: (Story, Sprint) -> Unit,
    modifier: Modifier = Modifier,
    cRad: Dp = 10.dp,
    cutoff: Dp = 30.dp,
) {
    Box(

    ) {
        Canvas(
            modifier = Modifier
                .padding(7.5.dp)
                .fillMaxWidth()
                .height(125.dp)
                .matchParentSize()
        ) {
            val clipPath = Path().apply {
                lineTo(size.width - cutoff.toPx(), 0f)
                lineTo(size.width, cutoff.toPx())
                lineTo(size.width, size.height)
                lineTo(0f, size.height)
                close()
            }

            clipPath(clipPath) {
                drawRoundRect(
                    color = Color(story.color),
                    topLeft = Offset(25f, 5f),
                    size = Size(125f, 25f),
                    style = Stroke(
                        width = 3.dp.toPx()
                    ),
                    cornerRadius = CornerRadius(cRad.toPx())
                )
                drawRoundRect(
                    color = Color(
                        ColorUtils.blendARGB(story.color, 0x000000, 0.2f)
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
                    //size = Size(125f, 75f),
                    cornerRadius = CornerRadius(cRad.toPx())
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = Story.colors,
                        center = center,
                        radius = 100f,
                    ),
                    radius = 100f,
                    center = center
                )
                drawArc(
                    brush = Brush.radialGradient(
                        colors = Story.colors)
                    ,
                    startAngle = 0f,
                    sweepAngle = 320f,
                    useCenter = true,
                    topLeft = Offset(250f, 50f),
                    size = Size(250f, 50f)
                )
                drawOval(
                    brush = Brush.radialGradient(
                        colors = Story.colors)
                    ,
                    topLeft = Offset(500f, 25f),
                    size = Size(150f, 75f)
                )
                drawLine(
                    color = LightCyan,
                    start = Offset(300f, 80f),
                    end = Offset(800f, 120f),
                    strokeWidth = 5.dp.toPx()
                )

            }
        }
        BoxWithConstraints(
            modifier = Modifier
                .drawBehind {

                }
        ) {
            val maxW = constraints.maxWidth * 0.75
            var preStatus by rememberSaveable { mutableStateOf(story.status) }
            var offsetX by rememberSaveable { mutableStateOf(initBoxPos(maxW, story)) }
            var offsetY by rememberSaveable { mutableStateOf(0f) }
            var posX by rememberSaveable { mutableStateOf(0f) }
            var posY by rememberSaveable { mutableStateOf(0f) }
            Card(
                modifier = Modifier
                    .padding(16.dp)
                    .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                    .background(color = ColdFrontGreen, shape = Shapes.medium)
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            posX = offsetX
                            posY = offsetY
                            //offsetX = processDragChange(posX, dragAmount.x, maxW, story)
                            if (preStatus != story.status) {
                                //onDraggedChange(story, story.status, story.done)
                                preStatus = story.status
                            }
                            offsetY += dragAmount.y
                        }
                    }
            )
            {
                Column(modifier = Modifier
                    .wrapContentSize()
                    .background(
//                        color = Color(
//                            ColorUtils.blendARGB(
//                                story.color, TiffanyBlue.toArgb(), 0.7f
//                            )
//                        ),
                        color = statToColor(story),
                        shape = Shapes.small
                    )
                    .padding(5.dp)

                ) {
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 0.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top) {
                        Icon(
                            modifier = Modifier
                                .scale(0.65f)
                                .offset(y = (-2).dp),
                            imageVector = iconPri(Priority.valueOf(story.priority)),
                            contentDescription = "Priority",
                            tint = MaterialTheme.colors.onSurface
                        )
                        IconButton(
                            onClick = { onOpenStoryDialog(story) },
                            modifier = Modifier
                                //.align(Alignment.CenterStart)
                                .scale(0.85f)
                                .offset(x = -(1).dp, y = (-20).dp)
                                .padding(5.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Default.Pending,
                                contentDescription = "Move Backlog Position",
                                tint = MaterialTheme.colors.onSurface
                            )
                        }

                        Text(
                            text = "${story.points}",
                            modifier = Modifier
                                //.align(Alignment.CenterEnd)
                                .offset(x = 10.dp),
                            fontWeight = FontWeight.W900,
                            style = MaterialTheme.typography.body1,
                            color = MaterialTheme.colors.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Clip
                        )
                        Text(
                            text = story.status,
                            color = Color(
                                ColorUtils.blendARGB(
                                    TropicalViolet.toArgb(), DarkGray.toArgb(), 0.5f)
                            ),
                            style = MaterialTheme.typography.body2,
                            overflow = TextOverflow.Clip,
                            fontSize = 15.sp,
                            softWrap = true,
                            modifier = Modifier
                                .scale(0.7f)
                                .offset(30.dp, 0.dp)
                        )

                        IconButton(
                            onClick = {
                                offsetX = moveBox(!isDone, maxW)
                                onCheckboxClick(story, !isDone, story.points)
                            },
                            modifier = Modifier
                                //.wrapContentSize()
                                .offset(10.dp, (-12).dp)
                                .statusBarsPadding()
                                .padding(0.dp)
                            //.align(Alignment.TopEnd)
                        ) {
                            Icon(
                                modifier = Modifier.scale(0.65f),
                                imageVector = if (story.done) Icons.Default.CheckCircle
                                else if (story.status == "In Progress") Icons.Default.RadioButtonChecked
                                else Icons.Default.RadioButtonUnchecked,
                                contentDescription = "Toggle Complete",
                                tint = MaterialTheme.colors.onSurface
                            )
                        }
                    }
                    Text(
                        text = if(story.cloned) "[CLONE] ${story.title}" else story.title,
                        color = DarkSlate,
                        onTextLayout ={

                        },
                        textDecoration = if (story.done) TextDecoration.LineThrough
                        else TextDecoration.None,
                        style = MaterialTheme.typography.body2,
                        textAlign = TextAlign.Center,
                        overflow = TextOverflow.Ellipsis,
                        softWrap = true,
                        maxLines = 1,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                    Text(
                        text = story.desc,
                        color = Grape,
                        textDecoration = if (story.done) TextDecoration.LineThrough
                        else TextDecoration.None,
                        style = MaterialTheme.typography.caption,
                        textAlign = TextAlign.Center,
                        overflow = TextOverflow.Ellipsis,
                        softWrap = true,
                        maxLines = 1,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                    Text(
                        text = story.body,
                        color = PurpleSage,
                        textDecoration = if (story.done) TextDecoration.LineThrough
                        else TextDecoration.None,
                        style = MaterialTheme.typography.caption,
                        textAlign = TextAlign.Center,
                        overflow = TextOverflow.Ellipsis,
                        softWrap = true,
                        maxLines = 1,
                        modifier = Modifier
                            .fillMaxWidth()
                    )

                    Row(modifier = Modifier
                        .fillMaxHeight(0.000001f)
                        .fillMaxWidth()
                        .padding(0.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        IconButton(
                            onClick = { onEditClick(story) },
                            modifier = Modifier
                                //.wrapContentSize()
                                .offset((-13).dp, 12.dp)
                                .padding(start = 0.dp)
                            //.align(Alignment.TopEnd)
                        ) {
                            Icon(
                                modifier = Modifier
                                    .scale(0.6f),
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit story",
                                tint = MaterialTheme.colors.onSurface
                            )
                        }
                        if(!story.assUri.isNullOrBlank()) {       // Assignee online avatar
                            AsyncImage(
                                model = story.assUri,
                                contentDescription = "Mini profile picture",
                                modifier = Modifier
                                    .offset(x = (-7.5).dp, y = (-5).dp)
                                    .size(15.dp)
                                    .clip(CircleShape),
                                //.align(Alignment.BottomCenter),
                                contentScale = ContentScale.Crop,
                                error = painterResource(id = R.drawable.box_red)
                            )
                        }
                        else{
                            Text(
                                text = story.assignee?.dropLast(
                                    if (story.assignee!!.length > 10) story.assignee!!.length - 10 else 0
                                )?: "Not Assigned",   // If user doesn't have avatar
                                // modifier = Modifier.align(Alignment.BottomCenter),
                                style = MaterialTheme.typography.body2,
                                color = Cobalt,
                                maxLines = 1,
                                overflow = TextOverflow.Clip,
                                textAlign = TextAlign.Center,
                                fontSize = 10.sp
                            )
                        }
                        if(!story.repUri.isNullOrBlank()) {       // Reporter online avatar
                            AsyncImage(
                                model = story.repUri,
                                contentDescription = "Mini profile picture",
                                modifier = Modifier
                                    .offset(x = (7.5).dp, y = (-5).dp)
                                    .size(15.dp)
                                    .clip(CircleShape),
                                //.align(Alignment.BottomCenter),
                                contentScale = ContentScale.Crop,
                                error = painterResource(id = R.drawable.box_red)
                            )
                        }
                        else{
                            Text(
                                text = story.reporter?.dropLast(
                                    if (story.reporter!!.length > 10) story.reporter!!.length - 10 else 0
                                )?: "No reporter",   // If user doesn't have avatar
                                // modifier = Modifier.align(Alignment.BottomCenter),
                                style = MaterialTheme.typography.body2,
                                color = Cobalt,
                                maxLines = 1,
                                overflow = TextOverflow.Clip,
                                textAlign = TextAlign.Center,
                                fontSize = 10.sp
                            )
                        }
                        IconButton(
                            onClick = { onDeleteClick(story) },
                            modifier = Modifier
                                //.wrapContentSize()
                                .offset(13.dp, 12.dp)
                                .padding(end = 0.dp)
                            //.align(Alignment.TopEnd)
                        ) {
                            Icon(
                                modifier = Modifier.scale(0.6f),
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Story",
                                tint = MaterialTheme.colors.onSurface
                            )
                        }

                    }

                }
            }
        }
    }
}

fun initBoxPos(width: Double, sto: Story): Float {
    if (sto.status == "Done") {
        return (width * 0.9).toFloat()
    }
    if (sto.status == "In Progress") {
        return (width * 0.4).toFloat()
    }
    return 0f
}

fun statToColor(story: Story): Color {
    return when(story.status) {
        "Done" -> {
            LightGreen
        }
        "In Progress" -> {
            Color(ColorUtils.blendARGB(
                ColdFrontGreen.toArgb(), LBGold.toArgb(), 0.7f)
            )
        }
        "TO DO" -> {
            TiffanyBlue
        }
        "Backlog" -> {
            BrownBear
        }
        else -> {
            PaleYellow
        }
    }
}