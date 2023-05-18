package com.example.agileandroidalpha.feature_board.presentation.search.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.ColorUtils
import com.example.agileandroidalpha.feature_board.presentation.sprint.msToDays
import com.example.agileandroidalpha.firebase.firestore.model.FireUser
import com.example.agileandroidalpha.firebase.firestore.model.Sprint
import com.example.agileandroidalpha.ui.theme.BottleGreen
import com.example.agileandroidalpha.ui.theme.BrownSand
import com.example.agileandroidalpha.ui.theme.Carbon
import com.example.agileandroidalpha.ui.theme.DarkCoffee
import com.example.agileandroidalpha.ui.theme.DarkGold
import com.example.agileandroidalpha.ui.theme.DarkScarlet
import com.example.agileandroidalpha.ui.theme.DesertSand
import com.example.agileandroidalpha.ui.theme.EarthGreen
import com.example.agileandroidalpha.ui.theme.GoldenYellow
import com.example.agileandroidalpha.ui.theme.GraniteSlate
import com.example.agileandroidalpha.ui.theme.Mocha
import com.example.agileandroidalpha.ui.theme.OjGold
import com.example.agileandroidalpha.ui.theme.PastelBlueAlt
import com.example.agileandroidalpha.ui.theme.PastelLilac
import com.example.agileandroidalpha.ui.theme.PurpleMaroon
import com.example.agileandroidalpha.ui.theme.RoseDust
import com.example.agileandroidalpha.ui.theme.SaffronOj
import com.example.agileandroidalpha.ui.theme.SapphireBlue
import com.example.agileandroidalpha.ui.theme.TealBlue
import com.example.agileandroidalpha.ui.theme.TiffanyBlue
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.min
import kotlin.random.Random
import kotlin.time.ExperimentalTime

@Composable
fun MiniSprintItem(
    sp: Sprint,
    modifier: Modifier = Modifier,
    cRad: Dp = 3.dp,
    cutoff: Dp = 7.5.dp,
){
    @OptIn(ExperimentalTime::class)
    fun formatDateDisplay(dt: Long?): String? = run {
        DateTimeFormatter
            .ofPattern("MMM dd yyyy")
            .format(LocalDate.ofEpochDay(msToDays(dt?:0)))
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
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

        val spTxt = "Created by : ${sp.creator?: "Unknown"}\nPO : ${sp.owner ?: "None"}\n" +
                "Manager : ${sp.manager ?: "None"}\n"
        val spT2 = "${sp.remPoints} / ${sp.totalPoints} points completed"
        val spStat = "Status: ${sp.status}\nResolution: ${sp.resolution}"
        val col = if (sp.isArchived) DarkCoffee else if (sp.isApproved) Mocha else if (sp.isReviewed) OjGold
        else if (sp.completed) SaffronOj else if (sp.started) DarkGold else if (sp.paused) DesertSand else RoseDust
        val sz = if (sp.started) 25.sp else if (sp.active == true) 20.sp else 15.sp
        Text(
            modifier = Modifier
                .align(Alignment.TopStart),
            text = "Sprint #${sp.id}",
            fontSize = 16.sp,
            color = Carbon
        )
        if (sp.cloned) {
            Text(
                modifier = Modifier
                    .background(PastelBlueAlt)
                    .align(Alignment.TopCenter),
                text = "CLONE",
                color = SapphireBlue,
                fontSize = 15.sp,
            )
        } else if (!sp.clones.isNullOrEmpty()) {
            val cl = sp.clones.toString()
            Text(
                modifier = Modifier
                    .background(PastelBlueAlt)
                    .align(Alignment.TopCenter),
                text = "CLONE of sprint(s) $cl",
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
        )
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround
        ){
            val app = if (sp.isArchived) "Archived" else if (sp.isApproved) "Approved"
                else if (sp.isReviewed) "Reviewed" else ""
            val appCol = if (sp.isArchived) DarkScarlet else if (sp.isApproved) EarthGreen
            else if (sp.isReviewed) GoldenYellow else BrownSand
            Text(
                text = app,
                color = appCol
            )
            if (!sp.title.isNullOrBlank())
            {
                Text(
                    text = "${sp.title}",
                    color = BottleGreen,
                    fontSize = 22.sp,
                )
            }
            else {
                Text(
                    text = "Untitled Sprint",
                    fontSize = 17.5.sp
                )
            }
            if (!sp.desc.isNullOrBlank()) {
                Text(
                    text = "${sp.desc?.length?.let { min(it, 50) }
                        ?.let { sp.desc?.substring(0, it) }}",
                    color = GraniteSlate,
                    fontSize = 12.sp,
                )
            }
            Text(
                modifier = Modifier.offset(x = 125.dp),
                text = "Start Date: ${formatDateDisplay(sp.startDate)}",
                color = TealBlue,
                fontWeight = FontWeight.Bold
            )
            Text(
                modifier = Modifier.offset(x = 125.dp),
                text = "End Date: ${formatDateDisplay(sp.endDate)}",
                color = PurpleMaroon,
                fontWeight = FontWeight.Bold
            )
            Text(
                modifier = Modifier.offset(x = 125.dp),
                text = "${sp.countdown} days remaining",
                color = PurpleMaroon,
                fontWeight = FontWeight.Bold
            )
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    modifier = Modifier,
                    text = spTxt,
                    color = appCol,
                    fontSize = 12.sp,
                )
                Text(
                    modifier = Modifier,
                    text = spT2,
                    color = col,
                    fontSize = 12.sp,
                )
            }
        }
    }
}