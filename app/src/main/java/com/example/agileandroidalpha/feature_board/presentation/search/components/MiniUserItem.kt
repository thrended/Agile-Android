package com.example.agileandroidalpha.feature_board.presentation.search.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.agileandroidalpha.R
import com.example.agileandroidalpha.firebase.firestore.model.FireUser
import com.example.agileandroidalpha.ui.theme.BottleGreen
import com.example.agileandroidalpha.ui.theme.Burgundy
import com.example.agileandroidalpha.ui.theme.Carbon
import com.example.agileandroidalpha.ui.theme.Chestnut
import com.example.agileandroidalpha.ui.theme.Cranberry
import com.example.agileandroidalpha.ui.theme.DarkAlmond
import com.example.agileandroidalpha.ui.theme.GraniteSlate
import com.example.agileandroidalpha.ui.theme.Midnight
import com.example.agileandroidalpha.ui.theme.Mocha
import com.example.agileandroidalpha.ui.theme.SaffronOj
import com.example.agileandroidalpha.ui.theme.Wood
import kotlin.random.Random

@Composable
fun MiniUserItem(
    ur: FireUser,
    modifier: Modifier = Modifier,
    hMultiplier: Float = 1f,
    cRad: Dp = 3.dp,
    cutoff: Dp = 7.5.dp,
) {
    Box(
        modifier = modifier
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
                    size = Size(1000f, 250f * hMultiplier),
                    cornerRadius = CornerRadius(cRad.toPx())
                )
//                drawRoundRect(
//                    color = Color(
//                        ColorUtils.blendARGB(TiffanyBlue.toArgb(), PastelLilac.toArgb(), 0.5f)
//                    ),
//                    topLeft = Offset(size.width / 2.25f, 15f),
//                    size = Size(175f, 125f),
//                    //size = Size(125f, 75f),
//                    cornerRadius = CornerRadius(cRad.toPx())
//                )
            }
        }
        if (ur.name == null && ur.email == null) {
            Text(
                text = "Unknown User",
                fontSize = 20.sp
            )
        } else {
            val uLvl = "User level: ${if (ur.name == null) "Anonymous User" else if(ur.isAdmin) "Administrator"
                else if(ur.isPowerUser) "Power User" else if(ur.isDisabled) "Disabled User"
                else if(ur.isVerified) "Verified User" else "Unverified User"}"
            val uLvlCol = if (ur.isHeadmaster) Midnight
                else if (ur.isAdmin) Burgundy else if (ur.isPowerUser) Chestnut
                else if (ur.isDisabled) Mocha else if (ur.isVerified) SaffronOj
                else if (ur.name == null) DarkAlmond else Wood
            val uLvLSz = if (ur.isHeadmaster) 22.sp else if (ur.isAdmin) 18.sp
                else if (ur.isPowerUser) 15.sp else if (ur.isVerified) 12.5.sp else 10.sp
            Text(
                modifier = Modifier
                    .align(Alignment.TopStart),
                text = "User #${ur.id}",
                fontSize = 16.sp,
                color = Carbon
            )
//            ur.photo?.let {
//                AsyncImage(
//                    model = ur.photo,
//                    contentDescription = "Profile picture",
//                    modifier = Modifier
//                        .align(Alignment.TopCenter)
//                        .size(50.dp)
//                        .clip(CircleShape),
//                    contentScale = ContentScale.Crop,
//                    error = painterResource(id = R.drawable.profsad_small)
//                )
//            }?: Text(modifier = Modifier.align(Alignment.TopCenter), text = "No photo")
//            Text(
//                modifier = Modifier
//                    .align(Alignment.TopEnd),
//                text = uLvl,
//                color = uLvlCol,
//                fontSize = uLvLSz,
//            )
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceAround
            ){
                val verified = if (ur.isHeadmaster) "Headmaster\n⭐⭐⭐⭐⭐⭐⭐⭐⭐⭐⭐⭐"
                    else if (ur.isAdmin) "Administrator\n⭐⭐⭐⭐⭐⭐⭐⭐⭐"
                    else if (ur.isModerator) "Moderator ⭐⭐⭐⭐⭐⭐"
                    else if (ur.isPowerUser) "Power User ⭐⭐⭐"
                    else if (ur.isVerified) "Verified User ⭐"
                    else if (ur.name == null) "Anonymous User" else "Unverified User ☆"
                val verCol = if (ur.isVerified) SaffronOj else Cranberry
                ur.photo?.let {
                    AsyncImage(
                        model = ur.photo,
                        contentDescription = "Profile picture",
                        modifier = Modifier
//                            .align(Alignment.TopCenter)
                            .size(50.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop,
                        error = painterResource(id = R.drawable.profsad_small)
                    )
                }?: Text(modifier = Modifier, text = "No photo")
                Text(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally),
                    text = verified,
                    color = uLvlCol,
                    fontSize = uLvLSz,
                )
//                Text(
//                    text = verified,
//                    color = verCol
//                )
                if (!ur.name.isNullOrBlank())
                {
                    Text(
                        text = "${ur.name}",
                        color = BottleGreen,
                        fontSize = 22.sp,
                    )
                }
                if (!ur.email.isNullOrBlank()) {
                    Text(
                        text = "${ur.email}",
                        color = GraniteSlate,
                        fontSize = 16.sp,
                    )
                }
            }
        }
    }
}