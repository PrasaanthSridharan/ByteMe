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
    abstract fun insert(transcript_words: TranscriptWordsRoom)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(transcript_words: Collection<TranscriptWordsRoom>)

    @Query("DELETE from transcript_words")
    abstract fun deleteAll()

    @Query("SELECT * from transcript_words WHERE recording_id = :recording_id AND transcript_index = :index")
    abstract fun search(recording_id: Long, index: Int): List<TranscriptWordsRoom>

    @Query("DELETE FROM transcript_words WHERE recording_id = :recording_id")
    abstract fun removeWordsForRecording(recording_id: Long)
}