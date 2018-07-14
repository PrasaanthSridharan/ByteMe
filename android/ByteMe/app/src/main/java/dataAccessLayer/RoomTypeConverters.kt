package dataAccessLayer

import android.arch.persistence.room.TypeConverter
import java.text.SimpleDateFormat
import java.util.Date

object RoomTypeConverters {
    val ISO8601_SDF = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")

    @TypeConverter
    @JvmStatic
    fun dateToISO8601 (date: Date): String = ISO8601_SDF.format(date)

    @TypeConverter
    @JvmStatic
    fun iso8601ToDate (dateStr: String): Date = ISO8601_SDF.parse(dateStr)
}