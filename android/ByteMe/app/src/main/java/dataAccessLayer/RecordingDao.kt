package dataAccessLayer

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query
import businessLayer.RecordingRoom

@Dao
interface RecordingDao {
    @Query("SELECT * from recordings ORDER BY created DESC")
    fun getAll(): List<RecordingRoom>

    @Insert(onConflict = REPLACE)
    fun insert(recording: RecordingRoom)

    @Insert(onConflict = REPLACE)
    fun insert(recordings: Array<RecordingRoom>)

    @Query("DELETE from recordings")
    fun deleteAll()

    @Query("SELECT * from recordings WHERE transcript LIKE :query")
    fun searchTranscripts(query: String): List<RecordingRoom>

    @Query("UPDATE recordings SET transcript = :transcript WHERE id = :recordingId")
    fun addTranscript(recordingId: Long, transcript: String)
}