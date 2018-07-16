package businessLayer

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey
import java.util.*


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