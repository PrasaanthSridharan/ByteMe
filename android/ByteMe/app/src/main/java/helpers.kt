package helpers

import android.content.res.Resources
import android.support.annotation.ColorInt
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.app.AppCompatActivity

/**
 * Utility function for converting a number of milliseconds to a string like "20:21".
 * Haven't really tested this; probably not bullet-proof (or idiot-proof for that matter :P)
 */
fun timeToString(time: Long): String {
    val allSeconds = time / 1000
    val seconds: Long = allSeconds % 60
    val minutes: Long = (allSeconds / 60) % 60
    val hours: Long = allSeconds / (60*60)
    if (hours > 0) {
        return listOf(hours, minutes, seconds)
                .joinToString(":") { it.toString().padStart(2, '0') }
    } else {
        return listOf(minutes, seconds)
                .joinToString(":") { it.toString().padStart(2, '0') }
    }
}

/**
 * Utility method which SHOULD BE INCLUDED IN ANDROID CORE >.<
 * Converts a color resource to a ColorInt
 * Must be called after onCreate so that resources is defined.
 * Ex: colorFromId(R.color.flag_red)
 */
@ColorInt
fun AppCompatActivity.colorFromId(res_id: Int, theme: Resources.Theme? = null): Int {
    return ResourcesCompat.getColor(resources, res_id, theme)
}

/**
 * Logic used to create the default recording names (like "Recording 0042")
 */
object DefaultRecordingName {
    const val padding = 4
    val regex = Regex("""^Recording \d{$padding}""" + "\$")
    fun ithName(i: Int): String = "Recording ${i.toString().padStart(padding, '0')}"
}