package com.example.agileandroidalpha.feature_board.presentation.users

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.agileandroidalpha.feature_board.domain.model.SprintRoom
import com.example.agileandroidalpha.feature_board.domain.model.TaskAndSubtasks
import com.example.agileandroidalpha.feature_board.domain.model.UserBrief

@Composable
fun UserItem(
    user: UserBrief,
    tasks: List<TaskAndSubtasks>,
    sprints: List<SprintRoom>,
    modifier: Modifier = Modifier,
    cRad: Dp = 10.dp,
    cutoff: Dp = 30.dp,
) {
    BoxWithConstraints(
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box()
            {
            Column(
                modifier = Modifier.fillMaxWidth(0.5f),
                verticalArrangement = Arrangement.SpaceAround,
                horizontalAlignment = Alignment.Start
            ) {
                Text(text = "${user.userId}" )
                Text(text = user.username)
                Text(text = user.password)
                user.email?.let { Text(text = it) }
                user.privilegeLvl?.let { Text(text = "$it" ) }
                user.admin?.let { Text(text = if (it) "Yes" else "No") }
                Text(text = if (user.active) "Yes" else "No")
            }
            Column(
                modifier = Modifier.fillMaxWidth(0.5f),
                verticalArrangement = Arrangement.SpaceAround,
                horizontalAlignment = Alignment.End
            ) {
                tasks.forEach { t ->
                    Text(text = "${t.task.taskId}" )
                    Text(text = t.task.title)
                    t.task.dod?.let { Text(text = it) }
                    Text(text = "${t.task.points}")
                    Text(text = "${t.task.priority}")
                    Text(text = t.task.status)
                    Text(text = "${t.task.sprintId}")
                    Text(text = "${t.task.assigneeId}")
                    Text(text = "${t.task.reporterId}")
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        t.subtasks./*map {s -> s.subtask }.*/forEach { s ->
                            Text(text = "${s.subId}")
                            Text(text = "${s.parentId}")
                            Text(text = s.title)
                            Text(text = "${s.points}")
                            Text(text = "${s.priority}")
                            Text(text = s.status)
                            Text(text = "${s.sprintId}")
                            Text(text = "${s.userId}")
                        }
                    }
                }
            }
            }
        }
    }
}