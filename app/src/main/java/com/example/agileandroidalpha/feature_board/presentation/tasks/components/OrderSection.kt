package com.example.agileandroidalpha.feature_board.presentation.tasks.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.agileandroidalpha.R
import com.example.agileandroidalpha.feature_board.domain.util.OrderType
import com.example.agileandroidalpha.feature_board.domain.util.TaskOrder

@Composable
fun OrderSection(
    modifier: Modifier = Modifier,
    taskOrder: TaskOrder = TaskOrder.Date(OrderType.Descending),
    onOrderChange: (TaskOrder) -> Unit
) {
    Column(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            DefaultRadioButton(
                text = stringResource(R.string.sort_title),
                selected = taskOrder is TaskOrder.Title,
                onSelect = { onOrderChange(TaskOrder.Title(taskOrder.type)) }
            )
            Spacer(modifier = Modifier.width(4.dp))
            DefaultRadioButton(
                text = stringResource(R.string.sort_priority),
                selected = taskOrder is TaskOrder.Priority,
                onSelect = { onOrderChange(TaskOrder.Priority(taskOrder.type)) }
            )
            Spacer(modifier = Modifier.width(4.dp))
            DefaultRadioButton(
                text = stringResource(R.string.sort_points),
                selected = taskOrder is TaskOrder.Points,
                onSelect = { onOrderChange(TaskOrder.Points(taskOrder.type)) }
            )
        }
        //Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            DefaultRadioButton(
                text = stringResource(R.string.sort_ass),
                selected = taskOrder is TaskOrder.Assignee,
                onSelect = { onOrderChange(TaskOrder.Assignee(taskOrder.type)) }
            )
            Spacer(modifier = Modifier.width(6.dp))
            DefaultRadioButton(
                text = stringResource(R.string.sort_rep),
                selected = taskOrder is TaskOrder.Reporter,
                onSelect = { onOrderChange(TaskOrder.Reporter(taskOrder.type)) }
            )
            Spacer(modifier = Modifier.width(6.dp))
            DefaultRadioButton(
                text = stringResource(R.string.sort_cre),
                selected = taskOrder is TaskOrder.Creator,
                onSelect = { onOrderChange(TaskOrder.Creator(taskOrder.type)) }
            )
        }
        //Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            DefaultRadioButton(
                text = stringResource(R.string.sort_id),
                selected = taskOrder is TaskOrder.ID,
                onSelect = { onOrderChange(TaskOrder.ID(taskOrder.type)) }
            )
            Spacer(modifier = Modifier.width(4.dp))
            DefaultRadioButton(
                text = stringResource(R.string.sort_body),
                selected = taskOrder is TaskOrder.Content,
                onSelect = { onOrderChange(TaskOrder.Content(taskOrder.type)) }
            )
            Spacer(modifier = Modifier.width(4.dp))
            DefaultRadioButton(
                text = stringResource(R.string.sort_color),
                selected = taskOrder is TaskOrder.Color,
                onSelect = { onOrderChange(TaskOrder.Color(taskOrder.type)) }
            )
            Spacer(modifier = Modifier.width(4.dp))
            DefaultRadioButton(
                text = stringResource(R.string.sort_size),
                selected = taskOrder is TaskOrder.Size,
                onSelect = { onOrderChange(TaskOrder.Size(taskOrder.type)) }
            )

        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            DefaultRadioButton(
                text = stringResource(R.string.sort_cred),
                selected = taskOrder is TaskOrder.Created,
                onSelect = { onOrderChange(TaskOrder.Created(taskOrder.type)) }
            )
            Spacer(modifier = Modifier.width(3.dp))
            DefaultRadioButton(
                text = stringResource(R.string.sort_mod),
                selected = taskOrder is TaskOrder.Date,
                onSelect = { onOrderChange(TaskOrder.Date(taskOrder.type)) }
            )
            Spacer(modifier = Modifier.width(3.dp))
            DefaultRadioButton(
                text = stringResource(R.string.sort_acc),
                selected = taskOrder is TaskOrder.Accessed,
                onSelect = { onOrderChange(TaskOrder.Accessed(taskOrder.type)) }
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text("Sort By:")
            Spacer(modifier = Modifier.width(5.dp))
            DefaultRadioButton(
                text = stringResource(R.string.sort_asc),
                selected = taskOrder.type is OrderType.Ascending,
                onSelect = { onOrderChange(taskOrder.copy(OrderType.Ascending)) }
            )
            Spacer(modifier = Modifier.width(5.dp))
            DefaultRadioButton(
                text = stringResource(R.string.sort_desc),
                selected = taskOrder.type is OrderType.Descending,
                onSelect = { onOrderChange(taskOrder.copy(OrderType.Descending)) }
            )
        }
    }

}