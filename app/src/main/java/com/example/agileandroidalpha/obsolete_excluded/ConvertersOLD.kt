@file:OptIn(ExperimentalTime::class)

package com.example.agileandroidalpha.obsolete_excluded

import androidx.room.TypeConverter
import java.util.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime

class ConvertersOLD {

    @TypeConverter
    fun tymToDate(tym: Long?): Date? {
        return tym?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTym(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun strToPass(pw: String?): PasswordOLD? {
        return pw?.let { PasswordOLD(it) }
    }

    @TypeConverter
    fun passToStr(pw: PasswordOLD?): String? {
        return pw?.toString()
    }

    @TypeConverter
    fun tymToDur(tym: Double?): Duration? {
        return tym?.let { tym.hours }
    }

    @TypeConverter
    fun durToTym(dur: Duration?): Double? {
        return dur?.toDouble(DurationUnit.HOURS)
    }
}