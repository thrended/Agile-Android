package com.example.agileandroidalpha.obsolete_excluded

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun StatefulClickCounter(modifier: Modifier = Modifier) {
    var clicks by remember { mutableStateOf(0) }

    StatelessClickCounter(clicks,
        onIncrement = { clicks++ },
        onReset = { clicks = 0},
        modifier
    )
}
@Composable
fun StatefulDonutCounter(modifier: Modifier = Modifier) {
    var donuts by rememberSaveable { mutableStateOf(0) }
    var totaldonut : Long by rememberSaveable { mutableStateOf(0) }
    var simp by rememberSaveable { mutableStateOf(false) }

    StatelessDonutCounter(donuts, totaldonut, simp,
        onDonut = { simp = true; donuts += (100..100000000).random() },
        onDeposit = { totaldonut += donuts; donuts = 0; simp = !simp },
        modifier
    )
}

@Composable
fun StatelessClickCounter(
    clicks: Int,
    onIncrement: () -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier
)
{
    Column(modifier = modifier.padding(16.dp)) {

        if (clicks > 0) {
            Text(
                text = "You've clicked this button $clicks times.",
                modifier = modifier.padding(16.dp)
            )
        }
        Row(
            Modifier
                .padding(top = 8.dp)
                .align(CenterHorizontally)
        ) {
            Button(
                onClick = { onIncrement() },
                Modifier
                    .padding(start = 8.dp)
            ) {
                androidx.compose.material3.Text("I've been clicked $clicks times")
            }
            Button(onClick = { onReset() }, Modifier.padding(start = 8.dp)) {
                Text("Reset click count")
            }
        }
        if (clicks > 0) {
            Text(
                text = "Click the button to get ${clicks + 1} clicks",
                modifier = modifier
                    .padding(16.dp)
                    .align(CenterHorizontally)
            )
        }

    }
}

@Composable
fun StatelessDonutCounter(
    donuts: Int,
    totaldonut: Long,
    simp: Boolean,
    onDonut: () -> Unit,
    onDeposit: () -> Unit,
    modifier: Modifier = Modifier
)
{
    Column(modifier = modifier.padding(16.dp)) {

        Text(
            text = "You have donated $${donuts}.00 ",
            modifier = modifier
                .padding(16.dp)
                .align(CenterHorizontally),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
        )
        FilledTonalButton(
            onClick = { onDonut() },
            Modifier
                .padding(top = 8.dp)
                .align(CenterHorizontally),
            enabled = donuts < 1000000000,
        ) {

            if (donuts >= 1000000000) {
                Text(
                    text = "Deposit in bank first before donuting more",
                    modifier = modifier.padding(16.dp),
                )
            }
            else {
                androidx.compose.material3.Text(
                    text = "Click here to gong to the donation page",
                    modifier = modifier.padding(16.dp),
                    style = MaterialTheme.typography.labelLarge
                )
            }

        }
        if (simp) {
            Text(
                text = "Button Test",
                modifier = modifier
                    .padding(16.dp)
                    .align(CenterHorizontally)
            )
        }
        ElevatedButton(
            onClick = { onDeposit() },
            Modifier
                .padding(top = 24.dp)
                .align(CenterHorizontally),
            enabled = donuts > 0
        ) {
            if (donuts > 0) {
                Text(
                    text = "Another button test",
                    modifier = modifier
                        .padding(16.dp),
                    style = MaterialTheme.typography.labelLarge
                )
            } else {
                Text(
                    text = "Long descriptive message",
                    modifier = modifier
                        .padding(8.dp),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
        Text (
            text = "Current bank balance : $${totaldonut}.00",
            modifier = modifier
                .padding(16.dp)
                .align(CenterHorizontally),
            style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.ExtraBold),
        )
        if (totaldonut < 1000000000000){
            Text (
                text = "M still need more donations and free Xmas gifts",
                modifier = modifier
                    .padding(8.dp)
                    .align(CenterHorizontally)
            )
        }
        else {
            Text (
                text = "Richie rich",
                modifier = modifier
                    .padding(16.dp)
                    .align(CenterHorizontally)
            )
        }

    }
}

@Composable
fun SubTaskScreen(
    modifier: Modifier = Modifier,
    subtaskViewModel: SubTaskViewModel = viewModel()
) {
    Column(modifier = modifier) {
        //StatefulClickCounter()
        //StatefulDonutCounter()
        Row(modifier = modifier) {
            SubTasksList(
                list = subtaskViewModel.tasks,
                onChecked = { task, checked -> subtaskViewModel.changeTaskChecked(task, checked) },
                onEdit = {},
                onClose = { task -> subtaskViewModel.remove(task) }
            )
        }
    }
}

//private fun getSubTasks() = List(100) { i -> SubTask(i, "Hunt PGF # ${i+1}") }
/*
            var showTask by remember { mutableStateOf(true) }
            if (showTask) {
                SubTaskItem(
                    onClose = { showTask = false },
                    taskName = "Click the X to gtng rid of dese spam messages?"
                )
            }

 */