package com.example.agileandroidalpha.feature_board.domain.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.example.agileandroidalpha.Priority
import com.example.agileandroidalpha.R
import com.example.agileandroidalpha.ui.theme.AzureBlue
import com.example.agileandroidalpha.ui.theme.BlueHost
import com.example.agileandroidalpha.ui.theme.Blush
import com.example.agileandroidalpha.ui.theme.Bronze
import com.example.agileandroidalpha.ui.theme.BrownBear
import com.example.agileandroidalpha.ui.theme.CadillacPink
import com.example.agileandroidalpha.ui.theme.CottonCandy
import com.example.agileandroidalpha.ui.theme.Cream
import com.example.agileandroidalpha.ui.theme.CreamWhite
import com.example.agileandroidalpha.ui.theme.Daisy
import com.example.agileandroidalpha.ui.theme.DragonGreen
import com.example.agileandroidalpha.ui.theme.ElectricBlue
import com.example.agileandroidalpha.ui.theme.Granite
import com.example.agileandroidalpha.ui.theme.GreenTea
import com.example.agileandroidalpha.ui.theme.HarvestGold
import com.example.agileandroidalpha.ui.theme.Honeydew
import com.example.agileandroidalpha.ui.theme.IndianRed
import com.example.agileandroidalpha.ui.theme.LMintGreen
import com.example.agileandroidalpha.ui.theme.LRoseGreen
import com.example.agileandroidalpha.ui.theme.Lavender
import com.example.agileandroidalpha.ui.theme.LavenderBlue
import com.example.agileandroidalpha.ui.theme.LightAqua
import com.example.agileandroidalpha.ui.theme.LightGold
import com.example.agileandroidalpha.ui.theme.LightJade
import com.example.agileandroidalpha.ui.theme.LightPurpleBlue
import com.example.agileandroidalpha.ui.theme.LightSalmon
import com.example.agileandroidalpha.ui.theme.LightSlate
import com.example.agileandroidalpha.ui.theme.LightTeal
import com.example.agileandroidalpha.ui.theme.MagicMint
import com.example.agileandroidalpha.ui.theme.Marble
import com.example.agileandroidalpha.ui.theme.MetallicGreen
import com.example.agileandroidalpha.ui.theme.MillenniumJade
import com.example.agileandroidalpha.ui.theme.Mint
import com.example.agileandroidalpha.ui.theme.NorthernLights
import com.example.agileandroidalpha.ui.theme.OffWhite
import com.example.agileandroidalpha.ui.theme.PaleTurquoise
import com.example.agileandroidalpha.ui.theme.PastelRed
import com.example.agileandroidalpha.ui.theme.Peach
import com.example.agileandroidalpha.ui.theme.Pearl
import com.example.agileandroidalpha.ui.theme.Platinum
import com.example.agileandroidalpha.ui.theme.Raspberry
import com.example.agileandroidalpha.ui.theme.Roman
import com.example.agileandroidalpha.ui.theme.RoseGold
import com.example.agileandroidalpha.ui.theme.Saffron
import com.example.agileandroidalpha.ui.theme.Sand
import com.example.agileandroidalpha.ui.theme.SpringGreen
import com.example.agileandroidalpha.ui.theme.Tangerine
import com.example.agileandroidalpha.ui.theme.TurquoiseGreen
import java.time.LocalDateTime

@Entity(tableName = "subtask")
class Subtask(
    val title: String = "New Subtask",
    val content: String = "New Subtask",
    val desc: String = "A long internal description",
    var dod: String? = null,
    val timestamp: Long = 0,
    val points: Long = 0,
    val priority: Priority = Priority.Lowest,
    val color: Int = Color(0xFF81D8D0).toArgb(),
    var assignee: String? = "None",
    var assUid: String? = null,
    var assUri: String? = null,
    var reporter: String? = "None",
    var repUid: String? = null,
    var repUri: String? = null,
    val resolution: String? = null,
    var status: String = "TO DO",
    var cloned: Boolean = false,
    var done: Boolean = false,
    val creDate: LocalDateTime? = null,
    var modDate: LocalDateTime? = null,
    var accDate: LocalDateTime = LocalDateTime.now(),
    @ColumnInfo(defaultValue = "Subtask") val type: IssueType = IssueType.Subtask,
    var current: Boolean = true,
    var id: Long? = null,
    var uid: String? = null,
    var uri: String? = null,
    var userId: Long? = null,
    var origId: Long? = null,
    val creator: String? = null,
    @ColumnInfo(name = "createdBy") val createdBy: String? = null,
    @ColumnInfo(name = "assigneeId") var assigneeId: Long? = null,
    @ColumnInfo(name = "reporterId") var reporterId: Long? = null,
    @ColumnInfo(name = "sprintId") var sprintId: Long? = null,
    @ColumnInfo(name = "parentId") var parentId: Long? = null,
    @PrimaryKey(autoGenerate = true) var subId: Long? = null,
) {
    companion object {
        val colors = listOf(
            IndianRed, PastelRed, Tangerine, Bronze, LightSalmon, Saffron, HarvestGold, LightGold,
            GreenTea, SpringGreen, DragonGreen, LightJade, LMintGreen, LRoseGreen, MagicMint, Mint,
            MetallicGreen, TurquoiseGreen, LightAqua, LightTeal, LightSlate, ElectricBlue, AzureBlue,
            NorthernLights, PaleTurquoise, BlueHost, LightPurpleBlue, LavenderBlue, Lavender, Daisy,
            Raspberry, CadillacPink, RoseGold, Cream, CreamWhite, Peach, Honeydew, BrownBear, Sand,
            CottonCandy, OffWhite, Granite, Roman, Marble, MillenniumJade, Platinum, Pearl, Blush
        )
    }

    fun markClone(sid: Long, uid: Pair<Long, String>) = this.apply {
        userId = uid.first
        uri = uid.second
        sprintId = sid
        origId = subId
        subId = null
        cloned = true
    }
}

data class SubtaskBrief(
    val title: String,
    val content: String,
    val points: Long,
    val priority: Priority,
)

@Entity(tableName = "attach")
data class Attachment(
    @PrimaryKey(autoGenerate = true) val attachId: Long? = null,
    val id: Long? = null,
    val image: Long? = null,
    val url: String? = null,
    val caption: String? = null,
    val height: Long = 24,
    val width: Long = 24
)

class SubtaskWithImages(
    @Embedded val subtask: Subtask,
    @Relation(
        entity = Attachment::class,
        parentColumn = "subId",
        entityColumn = "attachId"
    )
    val images: List<Attachment>
)

enum class IssueType(val string: String, val icon: Int? = null) {
    Task("Task", R.drawable.box_green),
    Story("Story", R.drawable.box_green),
    Subtask("Sub-task", R.drawable.box_green),
    Impediment("Impediment", R.drawable.box_red),
    Bug("Bug", R.drawable.box_red),
    Feature("Feature"),
    Initiative("Initiative"),
    Issue("Other Issue"),
    Test("Test"),
    Spike("Spike"),
    ChangeRequest("Change Request"),
    Epic("Epic")
}

// potential other priority levels
// unassigned, nice, won't should, must, could

// other workflow statuses
// reopened, failed, resolved, closed, done, fixing, need clarification, ready for test, reviewing,
// demo, deployed to the integration environment, ready for sprint planning, rejected, suspended,
// under team review, ready for verification, funnel, hold for funding,
// requirement analysis, requirement backlog, requirement implementation, requirement review,
// deployed for full regression, ready for release planning, ready for review, backlog,
// selected fro development, review, inspection