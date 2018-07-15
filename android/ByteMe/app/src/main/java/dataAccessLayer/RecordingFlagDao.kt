package dataAccessLayer

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query
import businessLayer.RecordingFlagRoom

@Dao
abstract class RecordingFlagDao {
    @Query("SELECT * from recording_flags")
    abstract fun getAll(): List<RecordingFlagRoom>

    @Insert(onConflict = REPLACE)
    abstract fun insert(flag: RecordingFlagRoom)

    @Query("DELETE from recording_flags")
    abstract fun deleteAll()

    @Query("SELECT * from recording_flags WHERE label LIKE :query")
    abstract fun search(query: String): List<RecordingFlagRoom>
}