package com.example.agileandroidalpha

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Task
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.Divider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.ColorUtils
import com.example.agileandroidalpha.core.TopAppBar
import com.example.agileandroidalpha.ui.theme.BP
import com.example.agileandroidalpha.ui.theme.Cerulean
import com.example.agileandroidalpha.ui.theme.Emerald
import com.example.agileandroidalpha.ui.theme.PaleYellow
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.min
import kotlin.math.roundToInt

private const val SPLASH_WAIT_TIME: Long = 3000

@Composable
fun LandingScreen(
    modifier: Modifier = Modifier,
    showMsg: String? = null,
    onTimeout: () -> Unit,
) {

    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = "Agile Droid",
                icon = Icons.Filled.Task,
                onIconClick = {
                    scope.launch {
                        scaffoldState.drawerState.open()
                    }
                },
            )

        },
        bottomBar = {
//            BottomAppBar() {
//
//            }
            androidx.compose.material3.BottomAppBar(windowInsets = WindowInsets.navigationBars) {
                showMsg?.let{
                    Text(showMsg)
                }
            }
        },
        scaffoldState = scaffoldState,
    )
    { padding ->
    var ticks by remember { mutableStateOf(0) }
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {

        val captureTymOut by rememberUpdatedState(newValue = onTimeout)
        LaunchedEffect(true) {
            while(ticks < 125) {
                delay(SPLASH_WAIT_TIME / 125)
                ticks++
            }
            captureTymOut()
        }
        Image(painterResource(id = R.drawable.profsad2), contentDescription = "Welcome to Agile Droid")
    }

    Box(modifier = Modifier
        .padding(padding)
        .fillMaxWidth()
        .fillMaxHeight(0.2f)
        .offset(0.dp), contentAlignment = Alignment.BottomCenter) {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
        Text(
            text = "Welcome to Agile Droid!",
            //modifier = Modifier.offset(y = (-50).dp),
            fontSize = 22.sp,
            fontWeight = FontWeight.W900,
            color = BP
        )
        Spacer(modifier = Modifier.height(20.dp))
        LinearProgressIndicator(
            modifier = modifier
                .offset(0.dp),
            progress = ticks / 120f,
            color = Color( ColorUtils.blendARGB(PaleYellow.toArgb(), Emerald.toArgb(), ticks / 120f))
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Loading ${min(ticks.div(1.2).roundToInt(), 100)}% , Please Wait....",
            //modifier = Modifier.offset(y = (-50).dp),
            fontSize = 22.sp,
            fontWeight = FontWeight.W900,
            color = Cerulean
        )
        Divider(modifier = Modifier.offset(y = 50.dp))
        }
    }
    }
}