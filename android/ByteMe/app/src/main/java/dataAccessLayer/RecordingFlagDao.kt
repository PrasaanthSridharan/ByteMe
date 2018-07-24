package dataAccessLayer

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query
import businessLayer.RecordingFlagRoom

@Dao
abstract class RecordingFlagDao {
    @Query("SELECT * from recording_flags where recording_id = :recordingId ORDER BY time ASC")
    abstract fun getForRecording(recordingId: Long): List<RecordingFlagRoom>

    @Query("SELECT * from recording_flags")
    abstract fun getAll(): List<RecordingFlagRoom>

    @Insert(onConflict = REPLACE)
    abstract fun insert(flag: RecordingFlagRoom)

    @Insert(onConflict = REPLACE)
    abstract fun insert(flag: Collection<RecordingFlagRoom>)

    @Insert(onConflict = REPLACE)
    abstract fun insert(flags: Array<RecordingFlagRoom>)

    @Query("DELETE from recording_flags")
    abstract fun deleteAll()

    @Query("SELECT * from recording_flags WHERE label LIKE :query")
    abstract fun search(query: String): List<RecordingFlagRoom>

    @Query("SELECT * from recording_flags WHERE label LIKE :query AND recording_id = :recordingId")
    abstract fun searchRecording(recordingId: Long, query: String): List<RecordingFlagRoom>

    fun search(query: String, recordingId: Long?): List<RecordingFlagRoom> {
        return when(recordingId) {
            null -> search(query)
            else -> searchRecording(recordingId, query)
        }
    }
}