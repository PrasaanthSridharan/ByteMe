package businessLayer

import android.arch.persistence.room.*
import android.arch.persistence.room.ForeignKey.CASCADE
import android.support.annotation.ColorInt

@Deprecated("This is the class used before we switched to Room; update code accordingly.")
data class RecordingFlag(
        val time: Long, // ms from start of recording
        @ColorInt var color: Int, // ColorInt? How do I annotate this type?
        var label: String?
)

/**
 * A class to store information about a given flag; I guess this is the "model"
 */
@Entity(
        tableName = "recording_flags",
        foreignKeys = [
            ForeignKey(entity = RecordingRoom::class,
                parentColumns = ["id"],
                childColumns = ["recording_id"],
                onDelete = CASCADE)],
        indices = [
            Index("recording_id"), // foreign key
            Index("time"), // for sorting
            Index("label") // for searching
        ])
data class RecordingFlagRoom(
        @PrimaryKey(autoGenerate = true)
        val id: Long?,

        @ColumnInfo(name = "recording_id")
        val recordingId: Long,

        /* offset in ms from start of recording */
        val time: Long,

        @ColorInt val color: Int,

        val label: String?
)