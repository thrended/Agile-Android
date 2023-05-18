package com.example.agileandroidalpha.feature_board.presentation.search.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.ColorUtils
import coil.compose.AsyncImage
import com.example.agileandroidalpha.Priority
import com.example.agileandroidalpha.R
import com.example.agileandroidalpha.feature_board.presentation.tasks.components.iconPri
import com.example.agileandroidalpha.feature_board.presentation.tasks.components.paintPri
import com.example.agileandroidalpha.feature_board.presentation.tasks.components.toLogo
import com.example.agileandroidalpha.firebase.firestore.model.FireUser
import com.example.agileandroidalpha.firebase.firestore.model.Story
import com.example.agileandroidalpha.ui.theme.Blonde
import com.example.agileandroidalpha.ui.theme.BottleGreen
import com.example.agileandroidalpha.ui.theme.BronzeGold
import com.example.agileandroidalpha.ui.theme.Carbon
import com.example.agileandroidalpha.ui.theme.DarkBeige
import com.example.agileandroidalpha.ui.theme.DarkBlonde
import com.example.agileandroidalpha.ui.theme.DarkBlueGray
import com.example.agileandroidalpha.ui.theme.DarkCoffee
import com.example.agileandroidalpha.ui.theme.DarkGold
import com.example.agileandroidalpha.ui.theme.DarkRaspberry
import com.example.agileandroidalpha.ui.theme.DarkScarlet
import com.example.agileandroidalpha.ui.theme.ForestGreen
import com.example.agileandroidalpha.ui.theme.GoldenBlonde
import com.example.agileandroidalpha.ui.theme.GraniteSlate
import com.example.agileandroidalpha.ui.theme.PastelBlueAlt
import com.example.agileandroidalpha.ui.theme.PastelLilac
import com.example.agileandroidalpha.ui.theme.SaffronOj
import com.example.agileandroidalpha.ui.theme.SapphireBlue
import com.example.agileandroidalpha.ui.theme.TiffanyBlue
import com.example.agileandroidalpha.ui.theme.VelvetMaroon
import kotlin.math.min
import kotlin.random.Random

@Composable
fun MiniStoryItem(
    st: Story,
    modifier: Modifier = Modifier,
    cRad: Dp = 3.dp,
    cutoff: Dp = 7.5.dp,
){

    Box(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight(0.25f)
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val clipPath = Path().apply {
                lineTo(size.width - cutoff.toPx(), 0f)
                lineTo(size.width, cutoff.toPx())
                lineTo(size.width, size.height)
                lineTo(0f, size.height)
                close()
            }

            clipPath(clipPath) {
                drawRoundRect(
                    color = FireUser.colors[Random.nextInt(FireUser.colors.size)],
                    size = Size(1000f, 250f),
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
            }
        }

        val spTxt = "Created by : ${st.createdBy ?: "Unknown User"}\n"
        val spT2 = st.points
        val spStat = "Status: ${st.status}\nResolution: ${st.resolution}"
        val col = if (st.done) ForestGreen else if (st.status == "In Progress") BronzeGold
        else if (st.status != "TO DO") SaffronOj else VelvetMaroon
        val ptColor = if (spT2 > 40) DarkScarlet else if (spT2 > 20) DarkRaspberry else if (spT2 > 10) DarkCoffee
        else if (spT2 > 5) DarkGold else if (spT2 > 3) DarkBlonde else if (spT2 > 2) DarkBeige
        else if (spT2 > 1) GoldenBlonde else Blonde
        val sz = if (spT2 > 20) 25.sp else if (spT2 > 5) 20.sp else 16.sp
        val ic = (Priority.valueOf(st.priority).value < 2 || Priority.valueOf(st.priority).value > 7 )
        val decor = if (st.done) TextDecoration.LineThrough else TextDecoration.None
        if (ic) {
            Image(
                modifier = Modifier
                    .align(Alignment.TopStart),
                painter = painterResource(id = paintPri(Priority.valueOf(st.priority))),
                contentDescription = "Priority"
            )
        }
        else {
            Icon(
                modifier = Modifier
                    .align(Alignment.TopStart),
                imageVector = iconPri(Priority.valueOf(st.priority)),
                contentDescription = "Priority"
            )
        }
        Text(
            modifier = Modifier
                .align(Alignment.TopCenter),
            text = "$spT2",
            fontSize = sz,
            color = ptColor
        )
        if(toLogo(st.status) != -1) {
            Image(
                painterResource(id = toLogo(st.status)),
                contentDescription = "Issue Type Logo",
                modifier = Modifier
                    //.offset( x = 30.dp, y = (-10).dp )
                    .align(Alignment.TopCenter)
                    .size(30.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }
        if (st.cloned) {
            Text(
                modifier = Modifier
                    .background(PastelBlueAlt)
                    .align(Alignment.Center),
                text = "CLONE",
                color = SapphireBlue,
                fontSize = 15.sp,
            )
        }
        Text(
            modifier = Modifier
                .align(Alignment.TopEnd),
            text = spStat,
            color = col,
            fontSize = sz,
            textDecoration = decor
        )
        Text(
            modifier = Modifier
                .align(Alignment.CenterStart),
            text = "Story #${st.id}",
            fontSize = 16.sp,
            color = Carbon
        )
        if (st.cloned) {
            Text(
                modifier = Modifier
                    .background(PastelBlueAlt)
                    .align(Alignment.BottomStart),
                text = "CLONE",
                color = SapphireBlue,
                fontSize = 15.sp,
            )
        }
        st.assUri?.let {
            AsyncImage(
                model = st.assUri,
                contentDescription = "Mini profile picture",
                modifier = Modifier
                    .offset(x = (-15).dp, y = (-10).dp)
                    .size(30.dp)
                    .clip(CircleShape)
                    .align(Alignment.BottomCenter),
                contentScale = ContentScale.Crop,
                error = painterResource(id = R.drawable.box_red)
            )
        }

        st.repUri?.let {
            AsyncImage(
                model = st.repUri,
                contentDescription = "Mini profile picture",
                modifier = Modifier
                    .offset(x = (-15).dp, y = (-10).dp)
                    .size(30.dp)
                    .clip(CircleShape)
                    .align(Alignment.BottomCenter),
                contentScale = ContentScale.Crop,
                error = painterResource(id = R.drawable.box_red)
            )
        }
        Text(
            modifier = Modifier
                .align(Alignment.BottomEnd),
            text = spTxt,
            color = DarkBlueGray,
            fontSize = 12.sp,
        )
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround
        ){
            if (st.title.isNotBlank())
            {
                Text(
                    text = st.title.substring(0, min(st.title.length, 20)),
                    color = BottleGreen,
                    fontSize = 18.sp,
                    textDecoration = decor
                )
            }
            else {
                Text(
                    text = "Untitled Story",
                    fontSize = 15.5.sp,
                    textDecoration = decor
                )
            }
            if (st.desc.isNotBlank()) {
                Text(
                    text = st.desc.substring(0, min(st.desc.length, 40)) ,
                    color = GraniteSlate,
                    fontSize = 12.sp,
                    textDecoration = decor
                )
            }
            if (st.body.isNotBlank()) {
                Text(
                    text = st.body.substring(0, min(st.body.length, 60)) ,
                    color = GraniteSlate,
                    fontSize = 8.sp,
                    textDecoration = decor
                )
            }
            if (!st.dod.isNullOrBlank()) {
                Text(
                    text = st.dod?.substring(0, min(st.dod!!.length, 60))?: "" ,
                    color = GraniteSlate,
                    fontSize = 8.sp,
                    textDecoration = decor
                )
            }
        }
    }
}