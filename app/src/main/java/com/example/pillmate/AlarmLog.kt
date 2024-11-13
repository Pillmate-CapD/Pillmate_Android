package com.example.pillmate

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "alarm_log")
data class AlarmLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val message: String,
    val timestamp: Date // 알람 발생 시간
)