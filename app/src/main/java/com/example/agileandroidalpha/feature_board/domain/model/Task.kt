package com.example.agileandroidalpha.feature_board.domain.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.example.agileandroidalpha.Priority
import com.example.agileandroidalpha.Status
import com.example.agileandroidalpha.ui.theme.Algae
import com.example.agileandroidalpha.ui.theme.AliceBlue
import com.example.agileandroidalpha.ui.theme.AmaranthPink
import com.example.agileandroidalpha.ui.theme.AquaStone
import com.example.agileandroidalpha.ui.theme.Azure
import com.example.agileandroidalpha.ui.theme.AzureBlue
import com.example.agileandroidalpha.ui.theme.BabyBlue
import com.example.agileandroidalpha.ui.theme.Bee
import com.example.agileandroidalpha.ui.theme.Blonde
import com.example.agileandroidalpha.ui.theme.BlueAngel
import com.example.agileandroidalpha.ui.theme.BlueDiamond
import com.example.agileandroidalpha.ui.theme.BlueDress
import com.example.agileandroidalpha.ui.theme.BlueEyes
import com.example.agileandroidalpha.ui.theme.BlueGray
import com.example.agileandroidalpha.ui.theme.BlueGreen
import com.example.agileandroidalpha.ui.theme.BlueHost
import com.example.agileandroidalpha.ui.theme.BlueIvy
import com.example.agileandroidalpha.ui.theme.BlueJay
import com.example.agileandroidalpha.ui.theme.BlueKoi
import com.example.agileandroidalpha.ui.theme.BlueLagoon
import com.example.agileandroidalpha.ui.theme.BlueTurquoise
import com.example.agileandroidalpha.ui.theme.BlueZircon
import com.example.agileandroidalpha.ui.theme.Blush
import com.example.agileandroidalpha.ui.theme.BlushPink
import com.example.agileandroidalpha.ui.theme.BlushRed
import com.example.agileandroidalpha.ui.theme.Brass
import com.example.agileandroidalpha.ui.theme.BrightTeal
import com.example.agileandroidalpha.ui.theme.Bronze
import com.example.agileandroidalpha.ui.theme.BronzeGold
import com.example.agileandroidalpha.ui.theme.BrownBear
import com.example.agileandroidalpha.ui.theme.BrownSugar
import com.example.agileandroidalpha.ui.theme.BubbleGum
import com.example.agileandroidalpha.ui.theme.Butterfly
import com.example.agileandroidalpha.ui.theme.CadetBlue
import com.example.agileandroidalpha.ui.theme.CadillacPink
import com.example.agileandroidalpha.ui.theme.Celeste
import com.example.agileandroidalpha.ui.theme.Champagne
import com.example.agileandroidalpha.ui.theme.Cherry
import com.example.agileandroidalpha.ui.theme.Chocolate
import com.example.agileandroidalpha.ui.theme.ChromeGreen
import com.example.agileandroidalpha.ui.theme.ChromeWhite
import com.example.agileandroidalpha.ui.theme.Cinnamon
import com.example.agileandroidalpha.ui.theme.Coffee
import com.example.agileandroidalpha.ui.theme.ColdPurple
import com.example.agileandroidalpha.ui.theme.ColumbiaBlue
import com.example.agileandroidalpha.ui.theme.Copper
import com.example.agileandroidalpha.ui.theme.CopperRed
import com.example.agileandroidalpha.ui.theme.CoralBlue
import com.example.agileandroidalpha.ui.theme.CoralPeach
import com.example.agileandroidalpha.ui.theme.CottonCandy
import com.example.agileandroidalpha.ui.theme.Cream
import com.example.agileandroidalpha.ui.theme.CreamWhite
import com.example.agileandroidalpha.ui.theme.CrystalBlue
import com.example.agileandroidalpha.ui.theme.CyanOP
import com.example.agileandroidalpha.ui.theme.Daisy
import com.example.agileandroidalpha.ui.theme.DarkBlonde
import com.example.agileandroidalpha.ui.theme.DarkMint
import com.example.agileandroidalpha.ui.theme.DarkPink
import com.example.agileandroidalpha.ui.theme.DarkSalmon
import com.example.agileandroidalpha.ui.theme.DaySkyBlue
import com.example.agileandroidalpha.ui.theme.DeepRose
import com.example.agileandroidalpha.ui.theme.DeepSea
import com.example.agileandroidalpha.ui.theme.DeepTurquoise
import com.example.agileandroidalpha.ui.theme.DenimBlue
import com.example.agileandroidalpha.ui.theme.DirtyWhite
import com.example.agileandroidalpha.ui.theme.Divine
import com.example.agileandroidalpha.ui.theme.DragonGreen
import com.example.agileandroidalpha.ui.theme.DullSea
import com.example.agileandroidalpha.ui.theme.ElectricBlue
import com.example.agileandroidalpha.ui.theme.Emerald
import com.example.agileandroidalpha.ui.theme.FRLilac
import com.example.agileandroidalpha.ui.theme.GhostWhite
import com.example.agileandroidalpha.ui.theme.GlacialBlue
import com.example.agileandroidalpha.ui.theme.GoldenBlonde
import com.example.agileandroidalpha.ui.theme.GoldenSilk
import com.example.agileandroidalpha.ui.theme.Granite
import com.example.agileandroidalpha.ui.theme.GreenBlue
import com.example.agileandroidalpha.ui.theme.GreenTea
import com.example.agileandroidalpha.ui.theme.GreenThumb
import com.example.agileandroidalpha.ui.theme.GulfBlue
import com.example.agileandroidalpha.ui.theme.HarvestGold
import com.example.agileandroidalpha.ui.theme.HeavenlyBlue
import com.example.agileandroidalpha.ui.theme.Honeydew
import com.example.agileandroidalpha.ui.theme.IceColdGreen
import com.example.agileandroidalpha.ui.theme.Iceberg
import com.example.agileandroidalpha.ui.theme.IndianRed
import com.example.agileandroidalpha.ui.theme.JeansBlue
import com.example.agileandroidalpha.ui.theme.Jellyfish
import com.example.agileandroidalpha.ui.theme.LBAmaranth
import com.example.agileandroidalpha.ui.theme.LMintGreen
import com.example.agileandroidalpha.ui.theme.LRoseGreen
import com.example.agileandroidalpha.ui.theme.Lavender
import com.example.agileandroidalpha.ui.theme.LavenderBlue
import com.example.agileandroidalpha.ui.theme.LavenderPurple
import com.example.agileandroidalpha.ui.theme.LightAqua
import com.example.agileandroidalpha.ui.theme.LightCopper
import com.example.agileandroidalpha.ui.theme.LightCyan
import com.example.agileandroidalpha.ui.theme.LightDayBlue
import com.example.agileandroidalpha.ui.theme.LightFRBeige
import com.example.agileandroidalpha.ui.theme.LightGold
import com.example.agileandroidalpha.ui.theme.LightGreen
import com.example.agileandroidalpha.ui.theme.LightJade
import com.example.agileandroidalpha.ui.theme.LightPurpleBlue
import com.example.agileandroidalpha.ui.theme.LightRed
import com.example.agileandroidalpha.ui.theme.LightRose
import com.example.agileandroidalpha.ui.theme.LightSalmon
import com.example.agileandroidalpha.ui.theme.LightSapphire
import com.example.agileandroidalpha.ui.theme.LightSlate
import com.example.agileandroidalpha.ui.theme.LightSteelBlue
import com.example.agileandroidalpha.ui.theme.LightTeal
import com.example.agileandroidalpha.ui.theme.Lilac
import com.example.agileandroidalpha.ui.theme.LtOrange
import com.example.agileandroidalpha.ui.theme.LtSeaGreen
import com.example.agileandroidalpha.ui.theme.MacawBlueGreen
import com.example.agileandroidalpha.ui.theme.MagicMint
import com.example.agileandroidalpha.ui.theme.Marble
import com.example.agileandroidalpha.ui.theme.Mauve
import com.example.agileandroidalpha.ui.theme.MetallicGold
import com.example.agileandroidalpha.ui.theme.MetallicGreen
import com.example.agileandroidalpha.ui.theme.MetallicSilver
import com.example.agileandroidalpha.ui.theme.MiddayBlue
import com.example.agileandroidalpha.ui.theme.MillenniumJade
import com.example.agileandroidalpha.ui.theme.Mint
import com.example.agileandroidalpha.ui.theme.MintCream
import com.example.agileandroidalpha.ui.theme.MistyBlue
import com.example.agileandroidalpha.ui.theme.NorthernLights
import com.example.agileandroidalpha.ui.theme.OceanBlue
import com.example.agileandroidalpha.ui.theme.OffWhite
import com.example.agileandroidalpha.ui.theme.OrganicBrown
import com.example.agileandroidalpha.ui.theme.PaleBlueLily
import com.example.agileandroidalpha.ui.theme.PaleLilac
import com.example.agileandroidalpha.ui.theme.PaleMintGreen
import com.example.agileandroidalpha.ui.theme.PaleSilver
import com.example.agileandroidalpha.ui.theme.PaleTurquoise
import com.example.agileandroidalpha.ui.theme.Parchment
import com.example.agileandroidalpha.ui.theme.PastelBlueAlt
import com.example.agileandroidalpha.ui.theme.PastelBrown
import com.example.agileandroidalpha.ui.theme.PastelGreen
import com.example.agileandroidalpha.ui.theme.PastelLightBlue
import com.example.agileandroidalpha.ui.theme.PastelOrange
import com.example.agileandroidalpha.ui.theme.PastelRed
import com.example.agileandroidalpha.ui.theme.PastelViolet
import com.example.agileandroidalpha.ui.theme.Peach
import com.example.agileandroidalpha.ui.theme.PeachPink
import com.example.agileandroidalpha.ui.theme.Pearl
import com.example.agileandroidalpha.ui.theme.PeriwinklePink
import com.example.agileandroidalpha.ui.theme.Pinocchio
import com.example.agileandroidalpha.ui.theme.PlatSilver
import com.example.agileandroidalpha.ui.theme.Platinum
import com.example.agileandroidalpha.ui.theme.PowderBlue
import com.example.agileandroidalpha.ui.theme.PurpleDragon
import com.example.agileandroidalpha.ui.theme.PurpleThistle
import com.example.agileandroidalpha.ui.theme.PurpleWhite
import com.example.agileandroidalpha.ui.theme.RedOrange
import com.example.agileandroidalpha.ui.theme.RedPink
import com.example.agileandroidalpha.ui.theme.RedWhite
import com.example.agileandroidalpha.ui.theme.Rice
import com.example.agileandroidalpha.ui.theme.Roman
import com.example.agileandroidalpha.ui.theme.RoseGold
import com.example.agileandroidalpha.ui.theme.RosePurple
import com.example.agileandroidalpha.ui.theme.Saffron
import com.example.agileandroidalpha.ui.theme.Sage
import com.example.agileandroidalpha.ui.theme.Sand
import com.example.agileandroidalpha.ui.theme.SeaBlue
import com.example.agileandroidalpha.ui.theme.SeaTurtleGreen
import com.example.agileandroidalpha.ui.theme.Silk
import com.example.agileandroidalpha.ui.theme.SilkBlue
import com.example.agileandroidalpha.ui.theme.Simpy
import com.example.agileandroidalpha.ui.theme.SkyBlueDress
import com.example.agileandroidalpha.ui.theme.SlateBlue
import com.example.agileandroidalpha.ui.theme.SpringGreen
import com.example.agileandroidalpha.ui.theme.Sunrise
import com.example.agileandroidalpha.ui.theme.TanBrown
import com.example.agileandroidalpha.ui.theme.Tangerine
import com.example.agileandroidalpha.ui.theme.TiffanyBlue
import com.example.agileandroidalpha.ui.theme.Tomato
import com.example.agileandroidalpha.ui.theme.TronBlue
import com.example.agileandroidalpha.ui.theme.TurquoiseGreen
import com.example.agileandroidalpha.ui.theme.Vanilla
import com.example.agileandroidalpha.ui.theme.VeryLightGold
import com.example.agileandroidalpha.ui.theme.VeryPeri
import com.example.agileandroidalpha.ui.theme.ViolaPurple
import com.example.agileandroidalpha.ui.theme.Violet
import com.example.agileandroidalpha.ui.theme.Water
import com.example.agileandroidalpha.ui.theme.WatermelonPink
import com.example.agileandroidalpha.ui.theme.WhiteChocolate
import com.example.agileandroidalpha.ui.theme.WindowsBlue
import com.example.agileandroidalpha.ui.theme.Wisteria
import com.example.agileandroidalpha.ui.theme.YellowOj
import java.time.LocalDateTime

@Entity(
    tableName = "task_subtask",
    foreignKeys = [
        ForeignKey(
            entity = Task::class,
            parentColumns = ["taskId"],
            childColumns = ["taskId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Subtask::class,
            parentColumns = ["subId"],
            childColumns = ["subId"],
            onDelete = ForeignKey.CASCADE,
            //onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(
            value = ["taskId", "subId"],
            unique = true
        )
    ]
)
data class TaskWithSubtasks(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    @ColumnInfo(name = "taskId") var taskId: Int,
    @ColumnInfo(name = "subId", index = true) var subId: Int
)

@Entity(tableName = "task")
class Task(
    val title: String,
    val content: String,
    val desc: String,
    val dod: String? = null,
    val timestamp: Long,
    val points: Long,
    val priority: Priority,
    val color: Int,
    var assignee: String? = null,
    var assUid: String? = null,
    var assUri: String? = null,
    var reporter: String? = null,
    var repUid: String? = null,
    var repUri: String? = null,
    val resolution: String? = null,
    var status: String,
    var done: Boolean,
    var cloned: Boolean = false,
    val creDate: LocalDateTime? = null,
    var modDate: LocalDateTime? = null,
    var accDate: LocalDateTime = LocalDateTime.now(),
    @ColumnInfo(defaultValue = "Task") val type: IssueType = IssueType.Task,
    var current: Boolean = true,
    //var numSubtasks: Int = 0,
    var timeToComplete: Long = 0,
    val id: Long? = null,
    var uid: String? = null,
    var uri: String? = null,
    var userId: Long? = null,
    var origId: Long? = null,
    val creator: String? = null,
    @ColumnInfo(name = "createdBy") val createdBy: String? = null,
    @ColumnInfo(name = "sprintId") var sprintId: Long? = null,    // SprintRoom
    @ColumnInfo(name = "assigneeId") var assigneeId: Long? = null,    // Assignee
    @ColumnInfo(name = "reporterId") var reporterId: Long? = null,  // Reporter
    @PrimaryKey(autoGenerate = true) var taskId: Long? = null,
) {
    companion object {
        val colors = listOf(LBAmaranth, RedOrange, CopperRed, LightCopper, LightRose, VeryLightGold, IceColdGreen, PaleMintGreen,
            LightGreen, Emerald, DeepTurquoise, PaleBlueLily, GulfBlue, SeaBlue, PastelBlueAlt, BlueAngel,
            TiffanyBlue, BlueKoi, PowderBlue, DenimBlue, CrystalBlue, MiddayBlue, Butterfly, Iceberg,
            SkyBlueDress, ColumbiaBlue, BlueIvy, BlueDress, OceanBlue, BlueEyes, BlueJay, BlueGray,
            Simpy, BabyBlue, LightSapphire, Divine, Violet, ColdPurple, RedPink, AmaranthPink, MistyBlue, PaleSilver
        )
        val subColors = listOf(
            IndianRed, PastelRed, Tangerine, Bronze, LightSalmon, Saffron, HarvestGold, LightGold,
            GreenTea, SpringGreen, DragonGreen, LightJade, LMintGreen, LRoseGreen, MagicMint, Mint,
            MetallicGreen, TurquoiseGreen, LightAqua, LightTeal, LightSlate, ElectricBlue, AzureBlue,
            NorthernLights, PaleTurquoise, BlueHost, LightPurpleBlue, LavenderBlue, Lavender, Daisy,
            CadillacPink, RoseGold, Cream, CreamWhite, Peach, Honeydew, BrownBear, Sand,
            CottonCandy, OffWhite, Granite, Roman, Marble, MillenniumJade, Platinum, Pearl, Blush
        )
        val moreColors = listOf(
            Cherry, Tomato, DarkPink, WatermelonPink, BlushRed, DeepRose, LightRed, Sunrise,
            DarkSalmon, Copper, Cinnamon, PeachPink, PastelOrange, BrownSugar, YellowOj, MetallicGold,
            Bee, Brass, BronzeGold, Sage, LightFRBeige, PastelBrown, LtOrange, CoralPeach, GoldenBlonde,
            GoldenSilk, DarkBlonde, Champagne, Blonde, TanBrown, Vanilla, Parchment, MintCream, DirtyWhite,
            ChromeWhite, OrganicBrown, GreenThumb, PastelGreen, Algae, ChromeGreen, DarkMint, LtSeaGreen,
            DullSea, SeaTurtleGreen, AquaStone, GreenBlue, DeepSea, CadetBlue, MacawBlueGreen, BlueTurquoise,
            Jellyfish, BrightTeal, BlueGreen, Celeste, CyanOP, BlueLagoon, BlueDiamond, BlueZircon, TronBlue,
            PastelLightBlue, CoralBlue, HeavenlyBlue, JeansBlue, GlacialBlue, WindowsBlue, LightDayBlue,
            DaySkyBlue, SilkBlue, Azure, SlateBlue, LightCyan, GhostWhite, AliceBlue, Water, LightSteelBlue,
            LavenderBlue, BubbleGum, Silk, PastelViolet, ViolaPurple, VeryPeri, FRLilac, PaleLilac, LavenderPurple,
            Lilac, RosePurple, Mauve, PurpleDragon, BlushPink, Wisteria, PurpleThistle, PurpleWhite, PeriwinklePink,
            Pinocchio, RedWhite, Rice, WhiteChocolate, PlatSilver, MetallicSilver, BrownBear, Chocolate, Coffee,
        )
        val priorities = listOf(
            Priority.Showstopper, Priority.Urgent, Priority.Critical, Priority.Highest, Priority.High,
            Priority.Medium, Priority.Low, Priority.Lowest, Priority.Trivial, Priority.None
        )
        val statuses = listOf(
            Status.Unknown, Status.TO_DO, Status.Open, Status.MI, Status.Stalled, Status.InProgress,
            Status.Fixing, Status.Reviewing, Status.Done, Status.Approved, Status.Closed, Status.Archived
        ).map { s -> s.string}
    }

    fun markClone(sid: Long, uid: Pair<Long, String>) = this.apply {
        userId = uid.first
        uri = uid.second
        sprintId = sid
        origId = taskId
        taskId = null
        cloned = true
    }
}

data class TaskDetail(
    val project: String,
    val component: String,
    val versionsAffected: String,
    val versionFixed: String,
    val environment: String,
    val log: String,
    val comments: String
)

//    val creDate: Date? = Date(),
//    var modDate: Date? = Date(),
//    var accDate: Date? = Date(),

//@Entity(primaryKeys = ["taskId", "subId"])
//data class TaskSubtaskCrossRef(
//    val taskId: Int? = null,
//    val subId: Int? = null,
//)

class TaskAndSubtasks(
    @Embedded val task: Task,
    @Relation(
        entity = Subtask::class,
        parentColumn = "taskId",
        entityColumn = "parentId"
        //,associateBy = Junction(TaskSubtaskCrossRef::class)
    )
    val subtasks: List<Subtask/*WithImages*/>
) {
    companion object {
        fun create(task: Task, subtasks: List<Subtask>): TaskAndSubtasks = TaskAndSubtasks(task, subtasks)
        fun create(pair: Pair<Task, List<Subtask>>): TaskAndSubtasks = TaskAndSubtasks(pair.first, pair.second)
    }
}

@Entity(
    tableName = "task_subtask_old",
    primaryKeys = ["taskId", "subId"],
    foreignKeys = [
        ForeignKey(
            entity = Task::class,
            parentColumns = ["taskId"],
            childColumns = ["taskId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Subtask::class,
            parentColumns = ["subId"],
            childColumns = ["subId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class TaskWithSubtasksDBModel(
    var taskId: Int,
    @ColumnInfo(index = true)
    var subId: Int
)

class TaskWithSubtasksTMP(
    @Embedded val task: Task,
    @Relation(
        parentColumn = "taskId",
        entityColumn = "subId",
        associateBy = Junction(
            value = TaskWithSubtasksDBModel::class,
            parentColumn = "taskId",
            entityColumn = "subId"
        )
    )
    val subtasks: List<Subtask>
) {

}

class InvalidTaskException(message: String): Exception(message)