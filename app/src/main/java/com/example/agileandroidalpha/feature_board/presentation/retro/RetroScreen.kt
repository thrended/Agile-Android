package com.example.agileandroidalpha.feature_board.presentation.retro

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.agileandroidalpha.ui.theme.Plum
import com.example.agileandroidalpha.ui.theme.Vanilla

@Composable
fun RetroScreen(
    navController: NavController
)
{
    Box(
        modifier = Modifier
            .background(Vanilla),
        contentAlignment = Alignment.Center
    ) {
//                    Card(
//                        border = BorderStroke(10.dp, Turquoise),
//                        backgroundColor = Blueberry,
//                        modifier = Modifier.size(400.dp)
//                    ) {
        Text(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(top = 25.dp, start = 25.dp)
                .fillMaxWidth(0.9f),
            text = "Coming Soon . . . . . . maybe . . . . . . ",
            color = Plum,
            fontSize = 15.sp,
            fontWeight = FontWeight.W500,
            fontStyle = FontStyle.Italic,
            textAlign = TextAlign.Justify
        )
//                    }
    }
}