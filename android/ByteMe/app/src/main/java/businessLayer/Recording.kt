package businessLayer

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.*

@Deprecated("This is the class used before we switched to Room; update code accordingly.")
data class Recording(
        val title: String,
        val date: String,
        val duration: String
) {
    companion object {
        private val DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd")

        /** Convert a RecordingRoom object from the database to this temporary class. */
        fun fromRecordingRoom(rr: RecordingRoom): Recording {
            return Recording(
                    rr.name,
                    DATE_FORMAT.format(rr.created),
                    helpers.timeToString(rr.duration))
        }
    }
}

@Entity(
        tableName = "recordings",
        indices = [
            Index("created"), // for sorting
            Index("name"), // for search/sorting
            Index("transcript") // for searching
        ])
data class RecordingRoom(
        @PrimaryKey(autoGenerate = true)
        val id: Long?,

        val name: String,
        val path: String,

        // val flags: RecordingFlag[]

        val transcript: String?,

        val created: Date,

        /* Length in ms */
        val duration: Long
)