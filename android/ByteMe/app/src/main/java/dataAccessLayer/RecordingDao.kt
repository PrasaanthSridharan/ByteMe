package dataAccessLayer

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query
import businessLayer.RecordingRoom

@Dao
abstract class RecordingDao {
    @Query("SELECT * from recordings ORDER BY created DESC")
    abstract fun getAll(): List<RecordingRoom>

    @Insert(onConflict = REPLACE)
    abstract fun insert(recording: RecordingRoom): Long

    @Insert(onConflict = REPLACE)
    abstract fun insert(recordings: Array<RecordingRoom>)

    @Query("DELETE from recordings")
    abstract fun deleteAll()

    @Query("SELECT * from recordings WHERE transcript LIKE :query")
    abstract fun searchTranscripts(query: String): List<RecordingRoom>

    @Query("UPDATE recordings SET transcript = :transcript WHERE id = :recordingId")
    abstract fun addTranscript(recordingId: Long, transcript: String)
}