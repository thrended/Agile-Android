package com.example.agileandroidalpha.feature_board.presentation.sprint

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@Composable
fun NewSprintScreen(
        navController: NavController,
        id: Long,
        VM: NewSprintVM = hiltViewModel()
) {
        val state = VM.state.value
        val title = VM.title.value
        val desc = VM.desc.value
        val start = VM.start.value
        val end = VM.end.value
        val length = VM.length.value
        val dates = VM.dates
        val owner = VM.owner
        val manager = VM.manager

        val scaffoldState = rememberScaffoldState()
        val scope = rememberCoroutineScope()

        Scaffold(
                floatingActionButton =
                {
                        FloatingActionButton(
                                onClick = { /*TODO*/ },
                                backgroundColor = MaterialTheme.colors.primary,
                        ){
                                Icon(imageVector = Icons.Default.Save, contentDescription = "Save Sprint")
                        }
                },
                scaffoldState = scaffoldState
        ) { padding -> 16.dp
                Column(
                        modifier = Modifier
                                .fillMaxSize()
                                .padding(padding)
                )  {

                }
        }
}