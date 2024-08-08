package com.smg.kasirsmg.utils

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.Timestamp
import java.time.LocalDate
import java.time.ZoneId

@RequiresApi(Build.VERSION_CODES.O)
fun isTimestampToday(timestamp: Timestamp): Boolean {
    val timestampDate = timestamp.toDate().toInstant()
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
    val today = LocalDate.now()

    return timestampDate.isEqual(today)
}

@RequiresApi(Build.VERSION_CODES.O)
fun isTimestampYesterday(timestamp: Timestamp): Boolean {
    val timestampDate = timestamp.toDate().toInstant()
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
    val yesterday = LocalDate.now().minusDays(1)

    return timestampDate.isEqual(yesterday)
}

@RequiresApi(Build.VERSION_CODES.O)
fun isTimestampH2(timestamp: Timestamp): Boolean {
    val timestampDate = timestamp.toDate().toInstant()
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
    val yesterday = LocalDate.now().minusDays(2)

    return timestampDate.isEqual(yesterday)
}
@RequiresApi(Build.VERSION_CODES.O)
fun isTimestampH3(timestamp: Timestamp): Boolean {
    val timestampDate = timestamp.toDate().toInstant()
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
    val yesterday = LocalDate.now().minusDays(3)

    return timestampDate.isEqual(yesterday)
}

@RequiresApi(Build.VERSION_CODES.O)
fun isTimestampH4(timestamp: Timestamp): Boolean {
    val timestampDate = timestamp.toDate().toInstant()
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
    val yesterday = LocalDate.now().minusDays(4)

    return timestampDate.isEqual(yesterday)
}

@RequiresApi(Build.VERSION_CODES.O)
fun isTimestampH5(timestamp: Timestamp): Boolean {
    val timestampDate = timestamp.toDate().toInstant()
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
    val yesterday = LocalDate.now().minusDays(5)

    return timestampDate.isEqual(yesterday)
}

@RequiresApi(Build.VERSION_CODES.O)
fun isTimestampH6(timestamp: Timestamp): Boolean {
    val timestampDate = timestamp.toDate().toInstant()
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
    val yesterday = LocalDate.now().minusDays(6)

    return timestampDate.isEqual(yesterday)
}