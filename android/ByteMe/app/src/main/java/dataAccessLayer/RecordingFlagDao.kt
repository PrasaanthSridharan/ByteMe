package dataAccessLayer

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query
import businessLayer.RecordingFlagRoom
import businessLayer.RecordingRoom

@Dao
interface RecordingFlagDao {
    @Query("SELECT * from recording_flags")
    fun getAll(): List<RecordingFlagRoom>

    @Insert(onConflict = REPLACE)
    fun insert(flag: RecordingFlagRoom)

    @Query("DELETE from recording_flags")
    fun deleteAll()

    @Query("SELECT * from recording_flags WHERE label LIKE :query")
    fun search(query: String): List<RecordingFlagRoom>
}