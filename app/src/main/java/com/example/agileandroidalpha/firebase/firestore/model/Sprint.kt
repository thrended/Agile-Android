package com.example.agileandroidalpha.firebase.firestore.model

import androidx.compose.ui.graphics.toArgb
import com.example.agileandroidalpha.ui.theme.Algae
import com.example.agileandroidalpha.ui.theme.AliceBlue
import com.example.agileandroidalpha.ui.theme.AquaStone
import com.example.agileandroidalpha.ui.theme.Azure
import com.example.agileandroidalpha.ui.theme.Bee
import com.example.agileandroidalpha.ui.theme.Blonde
import com.example.agileandroidalpha.ui.theme.BlueDiamond
import com.example.agileandroidalpha.ui.theme.BlueGreen
import com.example.agileandroidalpha.ui.theme.BlueLagoon
import com.example.agileandroidalpha.ui.theme.BlueTurquoise
import com.example.agileandroidalpha.ui.theme.BlueZircon
import com.example.agileandroidalpha.ui.theme.BlushPink
import com.example.agileandroidalpha.ui.theme.BlushRed
import com.example.agileandroidalpha.ui.theme.Brass
import com.example.agileandroidalpha.ui.theme.BrightTeal
import com.example.agileandroidalpha.ui.theme.BronzeGold
import com.example.agileandroidalpha.ui.theme.BrownBear
import com.example.agileandroidalpha.ui.theme.BrownSugar
import com.example.agileandroidalpha.ui.theme.BubbleGum
import com.example.agileandroidalpha.ui.theme.CadetBlue
import com.example.agileandroidalpha.ui.theme.Celeste
import com.example.agileandroidalpha.ui.theme.Champagne
import com.example.agileandroidalpha.ui.theme.Cherry
import com.example.agileandroidalpha.ui.theme.Chocolate
import com.example.agileandroidalpha.ui.theme.ChromeGreen
import com.example.agileandroidalpha.ui.theme.ChromeWhite
import com.example.agileandroidalpha.ui.theme.Cinnamon
import com.example.agileandroidalpha.ui.theme.Coffee
import com.example.agileandroidalpha.ui.theme.Copper
import com.example.agileandroidalpha.ui.theme.CoralBlue
import com.example.agileandroidalpha.ui.theme.CoralPeach
import com.example.agileandroidalpha.ui.theme.CyanOP
import com.example.agileandroidalpha.ui.theme.DarkBlonde
import com.example.agileandroidalpha.ui.theme.DarkMint
import com.example.agileandroidalpha.ui.theme.DarkPink
import com.example.agileandroidalpha.ui.theme.DarkSalmon
import com.example.agileandroidalpha.ui.theme.DaySkyBlue
import com.example.agileandroidalpha.ui.theme.DeepRose
import com.example.agileandroidalpha.ui.theme.DeepSea
import com.example.agileandroidalpha.ui.theme.DirtyWhite
import com.example.agileandroidalpha.ui.theme.DullSea
import com.example.agileandroidalpha.ui.theme.FRLilac
import com.example.agileandroidalpha.ui.theme.ForbiddenGud
import com.example.agileandroidalpha.ui.theme.GhostWhite
import com.example.agileandroidalpha.ui.theme.GlacialBlue
import com.example.agileandroidalpha.ui.theme.GoldenBlonde
import com.example.agileandroidalpha.ui.theme.GoldenSilk
import com.example.agileandroidalpha.ui.theme.GreenBlue
import com.example.agileandroidalpha.ui.theme.GreenThumb
import com.example.agileandroidalpha.ui.theme.HeavenlyBlue
import com.example.agileandroidalpha.ui.theme.Jellyfish
import com.example.agileandroidalpha.ui.theme.LavenderBlue
import com.example.agileandroidalpha.ui.theme.LavenderPurple
import com.example.agileandroidalpha.ui.theme.LightCyan
import com.example.agileandroidalpha.ui.theme.LightDayBlue
import com.example.agileandroidalpha.ui.theme.LightFRBeige
import com.example.agileandroidalpha.ui.theme.LightRed
import com.example.agileandroidalpha.ui.theme.LightSteelBlue
import com.example.agileandroidalpha.ui.theme.Lilac
import com.example.agileandroidalpha.ui.theme.LtOrange
import com.example.agileandroidalpha.ui.theme.LtSeaGreen
import com.example.agileandroidalpha.ui.theme.MacawBlueGreen
import com.example.agileandroidalpha.ui.theme.Mauve
import com.example.agileandroidalpha.ui.theme.MetallicGold
import com.example.agileandroidalpha.ui.theme.MetallicSilver
import com.example.agileandroidalpha.ui.theme.MintCream
import com.example.agileandroidalpha.ui.theme.OrganicBrown
import com.example.agileandroidalpha.ui.theme.PaleLilac
import com.example.agileandroidalpha.ui.theme.Parchment
import com.example.agileandroidalpha.ui.theme.PastelBrown
import com.example.agileandroidalpha.ui.theme.PastelGreen
import com.example.agileandroidalpha.ui.theme.PastelLightBlue
import com.example.agileandroidalpha.ui.theme.PastelOrange
import com.example.agileandroidalpha.ui.theme.PastelViolet
import com.example.agileandroidalpha.ui.theme.PeachPink
import com.example.agileandroidalpha.ui.theme.PeriwinklePink
import com.example.agileandroidalpha.ui.theme.Pinocchio
import com.example.agileandroidalpha.ui.theme.PlatSilver
import com.example.agileandroidalpha.ui.theme.PurpleDragon
import com.example.agileandroidalpha.ui.theme.PurpleThistle
import com.example.agileandroidalpha.ui.theme.PurpleWhite
import com.example.agileandroidalpha.ui.theme.RedWhite
import com.example.agileandroidalpha.ui.theme.Rice
import com.example.agileandroidalpha.ui.theme.RosePurple
import com.example.agileandroidalpha.ui.theme.Sage
import com.example.agileandroidalpha.ui.theme.SeaTurtleGreen
import com.example.agileandroidalpha.ui.theme.Silk
import com.example.agileandroidalpha.ui.theme.SilkBlue
import com.example.agileandroidalpha.ui.theme.SlateBlue
import com.example.agileandroidalpha.ui.theme.Sunrise
import com.example.agileandroidalpha.ui.theme.TanBrown
import com.example.agileandroidalpha.ui.theme.TiffanyBlue
import com.example.agileandroidalpha.ui.theme.Tomato
import com.example.agileandroidalpha.ui.theme.TronBlue
import com.example.agileandroidalpha.ui.theme.Vanilla
import com.example.agileandroidalpha.ui.theme.VeryPeri
import com.example.agileandroidalpha.ui.theme.ViolaPurple
import com.example.agileandroidalpha.ui.theme.Water
import com.example.agileandroidalpha.ui.theme.WatermelonPink
import com.example.agileandroidalpha.ui.theme.WhiteChocolate
import com.example.agileandroidalpha.ui.theme.WindowsBlue
import com.example.agileandroidalpha.ui.theme.Wisteria
import com.example.agileandroidalpha.ui.theme.YellowOj
import java.time.LocalDate
import java.time.Period
import kotlin.random.Random

class Sprint(
    val id: Long? = null,
    var sid: Long? = null,
    var uid: String? = null,
    var uri: String? = null,
    var origId: Long? = null,
    var title: String? = null,
    var desc: String? = null,
    var active: Boolean? = null,
    var started: Boolean = false,
    var paused: Boolean = false,
    var expired: Boolean = false,
    var cloned: Boolean = false,
    var status: String? = "Not Started",
    var color: Int = colors[Random.nextInt(colors.size)].toArgb(),
    var totalPoints: Long = 0,
    var remPoints: Long = 0,
    var startDate: Long? = 0,
    var endDate: Long? = 0,
    var duration: Int = 21,
    var elapsed: Int = 0,
    var countdown: Int = 21,
    var meetingTime: String? = "11:00",
    var reviewTime: String? = "10:30",
    var freq: Int? = 1,
    var backlogWt: Int = 10,
    val timestamp: Long = System.currentTimeMillis(),
    var uidList: List<String?>? = null,
    var uris: List<String>? = null,
    var stories: List<Long?>? = null,
    var subtasks: List<Long?>? = null,
    var logo: Int? = null,
    var pic: String? = null,
    var progress: Float? = null,
    var progressPct: Float? = null,
    var target: Float = 0.8f,
    var completed: Boolean = false,
    var resolution: String? = "Unresolved",
    var manual: Boolean? = null,
    val creator: String? = null,
    val createdBy: String? = null,
    val creatorID: Long? = null,
    var owner: String? = null,
    var ownerId: Long? = null,
    var ownerUid: String? = null,
    var ownerUri: String? = null,
    var manager: String? = null,
    var managerId: Long? = null,
    var managerUid: String? = null,
    var managerUri: String? = null,
    var projectId: Long? = null,
    var boardId: Long? = null,
    var componentId: Long? = null,
    var epicId: Long? = null,
    var icon: String? = null,
    var new: Boolean = true,
    var modDate: Long = System.currentTimeMillis(),
    var restrictions: List<String>? = null,
    var signatures: List<String>? = null,
    var comments: List<String>? = null,
    var log: List<String>? = null,
    var clones: List<Long>? = null,
    var associatedUsers: List<Long>? = null,
    var authorizedUsers: List<String>? = null,
    var reviewStatus: String? = "Not Reviewed",
    var approvalStatus: String? = "Not Approved",
    var projectStatus: String? = null,
    var archiveDate: Long = 0,
    @field:JvmField var isHidden: Boolean = false,
    @field:JvmField var isReviewed: Boolean = false,
    @field:JvmField var isApproved: Boolean = false,
    @field:JvmField var isArchived: Boolean = false,

    ) {
    companion object {
        fun create(): Sprint = Sprint()
        val colors = listOf(
            ForbiddenGud,
            Cherry,
            Tomato,
            DarkPink,
            WatermelonPink,
            BlushRed,
            DeepRose,
            LightRed,
            Sunrise,
            DarkSalmon,
            Copper,
            Cinnamon,
            PeachPink,
            PastelOrange,
            BrownSugar,
            YellowOj,
            MetallicGold,
            Bee,
            Brass,
            BronzeGold,
            Sage,
            LightFRBeige,
            PastelBrown,
            LtOrange,
            CoralPeach,
            GoldenBlonde,
            GoldenSilk,
            DarkBlonde,
            Champagne,
            Blonde,
            TanBrown,
            Vanilla,
            Parchment,
            MintCream,
            DirtyWhite,
            ChromeWhite,
            OrganicBrown,
            GreenThumb,
            PastelGreen,
            Algae,
            ChromeGreen,
            DarkMint,
            LtSeaGreen,
            DullSea,
            SeaTurtleGreen,
            AquaStone,
            GreenBlue,
            DeepSea,
            CadetBlue,
            MacawBlueGreen,
            BlueTurquoise,
            Jellyfish,
            BrightTeal,
            BlueGreen,
            Celeste,
            CyanOP,
            BlueLagoon,
            BlueDiamond,
            BlueZircon,
            TronBlue,
            PastelLightBlue,
            CoralBlue,
            HeavenlyBlue,
            TiffanyBlue,
            GlacialBlue,
            WindowsBlue,
            LightDayBlue,
            DaySkyBlue,
            SilkBlue,
            Azure,
            SlateBlue,
            LightCyan,
            GhostWhite,
            AliceBlue,
            Water,
            LightSteelBlue,
            LavenderBlue,
            BubbleGum,
            Silk,
            PastelViolet,
            ViolaPurple,
            VeryPeri,
            FRLilac,
            PaleLilac,
            LavenderPurple,
            Lilac,
            RosePurple,
            Mauve,
            PurpleDragon,
            BlushPink,
            Wisteria,
            PurpleThistle,
            PurpleWhite,
            PeriwinklePink,
            Pinocchio,
            RedWhite,
            Rice,
            WhiteChocolate,
            PlatSilver,
            MetallicSilver,
            BrownBear,
            Chocolate,
            Coffee,
        )
    }
    fun matchesSearchQuery(que: String): Boolean {
        if (que.isBlank()) return false
        val matching = listOfNotNull(
            title, desc, uid,
            title?.split(" ")?.first(), "id: $id", "id:$id",
            desc?.split(" ")?.first(), desc?.split(" ")?.last(),
            "id: $origId", "id:$origId",
            owner?.split(" ")?.first(), owner?.split(" ")?.last(),
            creator?.split(" ")?.first(), creator?.split(" ")?.last(),
            "sta: $status", "sta:$status",
            "clone : $cloned", "clone:$cloned"
        )
        return matching.any {
            it.contains(que, ignoreCase = true)
        }
    }

    fun start(
        length: Int = if(!this.paused) duration else this.countdown,
    ) {
        val today = LocalDate.now()
        val period = Period.ofDays(length)
        this.startDate = today.toEpochDay()
        this.endDate = (today + period).toEpochDay()
        //this.countdown = length
        //this.meetingTime = meet.atDate(today + Period.ofDays(1))
        this.active = true
        this.started = true
        this.paused = false
    }

    fun pause() {
        this.endDate = LocalDate.MAX.toEpochDay()
        this.meetingTime = "00:00"
        this.reviewTime = "00:00"
        this.started = false
        this.paused = true
    }

    fun update(): Boolean {
        if(active != true) {
            return false
        }
        val elapsed = Period.between(LocalDate.ofEpochDay(this.startDate!!), LocalDate.now()).days
        if (elapsed > this.elapsed) {
            this.elapsed = elapsed
            this.countdown = Period.between(LocalDate.now(), LocalDate.ofEpochDay(this.endDate!!)).days
            return true
        }
        return false
    }
}