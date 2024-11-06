package com.example.pillmate

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import java.util.Date

@Dao
interface AlarmLogDao {
    // AlarmLog 삽입 (suspend 함수로 지정)
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertLog(alarmLog: AlarmLog)

    // 특정 날짜에 해당하는 로그 검색
    @Query("SELECT * FROM alarm_log WHERE message = :message AND DATE(timestamp / 1000, 'unixepoch') = :date")
    suspend fun getLogsForDate(message: String, date: String): List<AlarmLog>

    // 특정 로그 검색
    @Query("SELECT * FROM alarm_log WHERE message = :message AND timestamp = :timestamp")
    suspend fun findLog(message: String, timestamp: Date): AlarmLog?

    // 모든 로그 가져오기 (suspend 키워드 사용)
    @Query("SELECT * FROM alarm_log ORDER BY timestamp DESC")
    suspend fun getAllLogs(): List<AlarmLog>

    // 모든 로그 가져오기 (LiveData로 반환)
    @Query("SELECT * FROM alarm_log ORDER BY timestamp DESC")
    fun getAllLogsLiveData(): LiveData<List<AlarmLog>>

    // 모든 로그 삭제
    @Query("DELETE FROM alarm_log")
    suspend fun deleteAllLogs()
}
