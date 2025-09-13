package com.veljkotosic.animalwatch.utility

import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Locale

fun Timestamp.addDays(days: Long) : Timestamp {
    val instant = Instant.ofEpochSecond(this.seconds, this.nanoseconds.toLong())
    val updated = instant.plus(days, ChronoUnit.DAYS)

    return Timestamp(updated.epochSecond, updated.nano)
}

fun Timestamp.takeDays(days: Long) : Timestamp {
    val instant = Instant.ofEpochSecond(this.seconds, this.nanoseconds.toLong())
    val updated = instant.minus(days, ChronoUnit.DAYS)

    return Timestamp(updated.epochSecond, updated.nano)
}

fun Timestamp.toDateString() : String {
    val formatter = SimpleDateFormat("dd.MM.yy.", Locale.getDefault())
    return formatter.format(this.toDate())
}