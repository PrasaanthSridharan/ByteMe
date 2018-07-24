package dataAccessLayer

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query
import android.database.Cursor
import businessLayer.RecordingRoom
import helpers.DefaultRecordingName

@Dao
abstract class RecordingDao {
    @Query("SELECT * from recordings WHERE id = :id")
    abstract fun get(id: Long): RecordingRoom

    @Query("SELECT * from recordings WHERE id IN(:ids)")
    abstract fun getMany(ids: Set<Long>): List<RecordingRoom>

    @Query("SELECT * from recordings ORDER BY created DESC")
    abstract fun getAll(): List<RecordingRoom>

    @Insert(onConflict = REPLACE)
    abstract fun insert(recording: RecordingRoom): Long

    @Insert(onConflict = REPLACE)
    abstract fun insert(recordings: Array<RecordingRoom>)

    @Query("DELETE from recordings")
    abstract fun deleteAll()

    @Query("SELECT * from recordings WHERE name LIKE :query")
    abstract fun searchNames(query: String): List<RecordingRoom>

    @Query("SELECT * from recordings WHERE transcript LIKE :query")
    abstract fun searchTranscripts(query: String): List<RecordingRoom>

    @Query("UPDATE recordings SET transcript = :transcript WHERE id = :recordingId")
    abstract fun addTranscript(recordingId: Long, transcript: String)

    @Query("SELECT name FROM recordings WHERE name LIKE 'Recording ____' ORDER BY name DESC")
    protected abstract fun getLargestRecordingName(): Cursor

    fun getNextRecordingName(): String {
        val recNamesCursor = getLargestRecordingName()
        if (recNamesCursor.moveToFirst()) {
            do {
                val name = recNamesCursor.getString(0)
                if (DefaultRecordingName.regex.matches(name)) {
                    val lastNum = name.split(' ')[1].toInt()
                    return DefaultRecordingName.ithName(lastNum + 1)
                }
            } while (recNamesCursor.moveToNext())
        }

        return DefaultRecordingName.ithName(0)
    }
}