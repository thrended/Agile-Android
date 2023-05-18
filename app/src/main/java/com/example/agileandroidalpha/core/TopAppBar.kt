package com.example.agileandroidalpha.core

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddTask
import androidx.compose.material.icons.filled.CallToAction
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TopAppBar(
    title: String,
    icon: ImageVector,
    onIconClick: () -> Unit,
    icon2: ImageVector = Icons.Filled.CallToAction,
    onIcon2Click: () -> Unit = {},
    icon3: ImageVector? = null,
    onIcon3Click: () -> Unit = {},
    icon4: ImageVector? = null,
    onIcon4Click: () -> Unit = {},
    icon5: ImageVector? = null,
    onIcon5Click: () -> Unit = {},
    icon6: ImageVector? = null,
    onIcon6Click: () -> Unit = {},
    title2: String? = null,
    title3: String? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(color = MaterialTheme.colors.secondary)
    ) {
        if (icon3 == null) Spacer(modifier = Modifier.width(50.dp))
        Image(
            imageVector = icon,
            contentDescription = "Top App Bar Icon",
            colorFilter = ColorFilter
                .tint(MaterialTheme.colors.onPrimary),
            modifier = Modifier
                .clickable(onClick = onIconClick)
                .padding(16.dp)
                .align(Alignment.CenterVertically)
        )
        icon3?.let {
            Image(
                imageVector = icon3,
                contentDescription = "Top App Bar Icon 3",
                colorFilter = ColorFilter
                    .tint(MaterialTheme.colors.onPrimary),
                modifier = Modifier
                    .clickable(onClick = onIcon3Click)
                    .padding(16.dp)
            )
        }
        Text(
            text = title,
            color = MaterialTheme.colors.onPrimary,
            style = TextStyle(
                fontWeight = FontWeight.Medium,
                fontSize = 20.sp,
                letterSpacing = 0.25.sp
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .align(Alignment.CenterVertically)
                .padding(start = 12.dp, end = 12.dp)
        )
        Image(
            imageVector = icon2,
            contentDescription = "Top App Bar Icon 2",
            colorFilter = ColorFilter
                .tint(MaterialTheme.colors.onPrimary),
            modifier = Modifier
                .clickable(onClick = onIcon2Click)
                .padding(16.dp)
                .align(Alignment.CenterVertically)
        )
        icon4?.let {
            Image(
                imageVector = icon4,
                contentDescription = "Top App Bar Icon 3",
                colorFilter = ColorFilter
                    .tint(MaterialTheme.colors.onPrimary),
                modifier = Modifier
                    .clickable(onClick = onIcon4Click)
                    .padding(16.dp)
            )
        }
    }
    icon6?.let {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(color = MaterialTheme.colors.secondary)
                .padding(start = 5.dp, end = 5.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            icon5?.let {
                Image(
                    imageVector = icon5,
                    contentDescription = "Top App Bar Icon 5",
                    colorFilter = ColorFilter
                        .tint(MaterialTheme.colors.onPrimary),
                    modifier = Modifier
                        .clickable(onClick = onIcon5Click)
                        .padding(16.dp)
                )
            }
            Image(
                imageVector = icon6,
                contentDescription = "Top App Bar Icon 6",
                colorFilter = ColorFilter
                    .tint(MaterialTheme.colors.onPrimary),
                modifier = Modifier
                    .clickable(onClick = onIcon6Click)
                    .padding(16.dp)
            )
        }
    }

    title2?.let {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .background(color = MaterialTheme.colors.secondary)
                .padding(start = 5.dp, end = 5.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Text(
                text = title2,
                color = MaterialTheme.colors.onPrimary,
                style = TextStyle(
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.5.sp,
                    letterSpacing = 0.225.sp
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .align(Alignment.CenterVertically)
                    .padding(start = 8.dp, end = 8.dp)
            )
            title3?.let {
                Text(
                    text = title3,
                    color = MaterialTheme.colors.onPrimary,
                    style = TextStyle(
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.5.sp,
                        letterSpacing = 0.225.sp
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .align(Alignment.CenterVertically)
                        .padding(start = 8.dp, end = 8.dp)
                )
            }
        }
    }

    }
}

@Preview
@Composable
private fun TopAppBarPreview() {
    MaterialTheme {
        TopAppBar(
            title = "AgileDroid",
            icon = Icons.Filled.AddTask,
            onIconClick = {}
        )
    }
}