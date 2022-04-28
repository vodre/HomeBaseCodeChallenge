package com.homebase.challenge.utils

import com.homebase.challenge.R
import org.joda.time.DateTime
import java.text.SimpleDateFormat
import java.util.*

fun String.toDateTime(pattern: String): DateTime =
    DateTime(SimpleDateFormat(pattern, Locale.getDefault()).parse(this))

fun DateTime.toStringFormat(): String {
    val dayWeek = this.dayOfWeek().asShortText
    val month = this.monthOfYear().asText
    val day = this.dayOfMonth
    val hour = this.hourOfDay
    val time = if (hour < 12) "AM" else "PM"
    return "$dayWeek, $month $day $hour $time"
}

fun Date.toFormattedString(format: String): String =
    SimpleDateFormat(format, Locale.getDefault()).format(this)

fun String.toColorResource(): Int =
    when (this.lowercase(Locale.getDefault())) {
        "red" -> R.color.red
        "blue" -> R.color.blue
        "green" -> R.color.green
        else -> R.color.gray
    }
