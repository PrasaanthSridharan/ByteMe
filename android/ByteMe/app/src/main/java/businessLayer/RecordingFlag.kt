package businessLayer

import android.support.annotation.ColorInt

/**
 * A class to store information about a given flag; I guess this is the "model"
 */
data class RecordingFlag(
        val time: Long, // ms from start of recording
        @ColorInt var color: Int, // ColorInt? How do I annotate this type?
        var label: String?
)