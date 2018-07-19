package helpers.businessLayer

import android.arch.persistence.room.*
import android.arch.persistence.room.ForeignKey.CASCADE
import businessLayer.RecordingRoom

/**
 * A class to store information about a given flag; I guess this is the "model"
 */
@Entity(
        tableName = "transcript_words",
        foreignKeys = [
            (ForeignKey(entity = RecordingRoom::class,
                parentColumns = ["id"],
                childColumns = ["recording_id"],
                onDelete = CASCADE))],
        indices = [
            Index("recording_id"), // foreign key
            Index("text"),
            Index("audio_offset")
        ])
data class TranscriptWordsRoom(
        @PrimaryKey(autoGenerate = true)
        val id: Long?,

        @ColumnInfo(name = "recording_id")
        val recordingId: Long,

        val text: String,

        @ColumnInfo(name = "audio_offset")
        val audioOffset: Long
)