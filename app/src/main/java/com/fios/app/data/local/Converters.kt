package com.fios.app.data.local

import androidx.room.TypeConverter
import com.fios.app.data.local.entities.LogType

class Converters {
    @TypeConverter
    fun fromLogType(value: LogType): String {
        return value.name
    }

    @TypeConverter
    fun toLogType(value: String): LogType {
        return LogType.valueOf(value)
    }
}
