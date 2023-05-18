package com.example.agileandroidalpha.obsolete_excluded

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.DraggableState
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Checkbox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
fun SubTaskItemOLD(
    taskName: String,
    checked: Boolean,
    status: String,
    onCheckedSTChange: (Boolean) -> Unit,
    onDraggedSTChange: (String) -> Unit,
    onEdit: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier){
        var offsetX by remember { mutableStateOf(0f) }
        var offsetY by remember { mutableStateOf(0f) }

        Box(
            Modifier //verticalAlignment = Alignment.CenterVertically
                .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                .background(Color.Blue)
                .size(50.dp)
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                    }
                }
        ) {
            Text(
                modifier = Modifier/*.weight(1f)*/.padding(start = 16.dp),
                text = taskName
            )
            Checkbox(
                checked = checked,
                onCheckedChange = onCheckedSTChange
            )
            DraggableState {  }
            IconButton(onClick = onEdit) {
                Icon(Icons.Filled.Edit, contentDescription = "Edit")
            }
            IconButton(onClick = onClose) {
                Icon(Icons.Filled.Close, contentDescription = "Close")
            }
        }
    }
}

//@Composable
//fun SubTaskItem(taskName: String, onClose: () -> Unit, modifier: Modifier = Modifier){
//    var checkedState by rememberSaveable { mutableStateOf(false) }
//    SubTaskItem(
//        taskName = taskName,
//        checked = checkedState,
//        onCheckedChange = { newValue -> checkedState = newValue },
//        onClose = onClose, // we will implement this later!
//        modifier = modifier,
//    )
//}

//Box(modifier) {
//                    Canvas(modifier = modifier.matchParentSize()) {
//
//                    }
//                    Box(modifier = Modifier
//                ) {
//                    var offsetX by remember { mutableStateOf(0f) }
//                    var offsetY by remember { mutableStateOf(0f) }
//
//                    Box(modifier = Modifier
//                        .padding(16.dp)
//                        .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
//                        .background(Color.Cyan)
//                        .pointerInput(Unit) {
//                            detectDragGestures { change, dragAmount ->
//                                change.consume()
//                                offsetX += dragAmount.x
//                                offsetY += dragAmount.y
//                            }
//                        }
//
//                    ) {
//                        Column(
//
//                        ) {
//
//                            Text(
//                                text = "test",
//                                style = MaterialTheme.typography.body1,
//                                overflow = TextOverflow.Ellipsis,
//                                modifier = Modifier
//                            )
//                            Text(
//                                text = "lol",
//                                style = MaterialTheme.typography.body2,
//                                overflow = TextOverflow.Ellipsis,
//                                modifier = Modifier
//                            )
//
//                        }
//                    }
//                }
//                }

//Text(
//                        modifier = Modifier
//                            .padding(end = 5.dp),
//                        text = if (sub.done) "✓ ${sub.title}"
//                                else sub.title,
//                        style = MaterialTheme.typography.body2,
//                        overflow = TextOverflow.Ellipsis,
//                        maxLines = 1,
//                    )