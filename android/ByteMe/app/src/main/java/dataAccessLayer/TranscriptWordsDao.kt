package helpers.dataAccessLayer

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import helpers.businessLayer.TranscriptWordsRoom

@Dao
abstract class TranscriptWordsDao {
    @Query("SELECT * from transcript_words")
    abstract fun getAll(): List<TranscriptWordsRoom>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(flag: TranscriptWordsRoom)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(flag: Collection<TranscriptWordsRoom>)

    @Query("DELETE from transcript_words")
    abstract fun deleteAll()

    @Query("SELECT * from transcript_words WHERE text LIKE :query")
    abstract fun search(query: String): List<TranscriptWordsRoom>
}