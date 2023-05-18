package com.example.agileandroidalpha.feature_board.domain.util

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Period
import java.util.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.DurationUnit

class Converters {
    @TypeConverter
    fun tymToDate(tym: Long?): Date? {
        return tym?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTym(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun tymToLocalTym(tym: Long?): LocalTime? {
        return tym?.let { LocalTime.ofNanoOfDay(tym) }
    }

    @TypeConverter
    fun localTymToTym(tym: LocalTime?): Long? {
        return tym?.toNanoOfDay()
    }

    @TypeConverter
    fun tymToLocalDate(tym: Long?): LocalDate? {
        return tym?.let { LocalDate.ofEpochDay(tym) }
    }

    @TypeConverter
    fun localDateToTym(date: LocalDate?): Long? {
        return date?.toEpochDay()
    }

    @TypeConverter
    fun daysToPeriod(days: Int?): Period? {
        return days?.let { Period.ofDays(days) }
    }

    @TypeConverter
    fun periodToDays(days: Period?): Int? {
        return days?.days
    }

    @TypeConverter
    fun tymToLocalDateTym(tym: Long?): LocalDateTime? {
        return tym?.let { LocalDate.ofEpochDay(tym).atTime(11, 30) }
    }

    @TypeConverter
    fun localDateTymToTym(date: LocalDateTime?): Long? {
        return date?.toLocalDate()?.toEpochDay()
    }

    @TypeConverter
    fun strToPass(pw: String?): Password? {
        return pw?.let { Password(it) }
    }

    @TypeConverter
    fun passToStr(pw: Password?): String? {
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

@JvmInline value class Password(private val s: String)