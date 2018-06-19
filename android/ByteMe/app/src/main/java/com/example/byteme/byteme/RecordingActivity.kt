package com.example.byteme.byteme

import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.support.annotation.ColorInt
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_recording.*
import kotlinx.android.synthetic.main.item_flag.view.*
import java.util.*
import kotlin.concurrent.timer


/**
 * A class to store information about a given flag; I guess this is the "model"
 */
data class RecordingFlag(
        val time: Long, // ms from start of recording
        @ColorInt var color: Int, // ColorInt? How do I annotate this type?
        var label: String?
)

/**
 * Utility function for converting a number of milliseconds to a string like "00:20:21".
 * Haven't really tested this; probably not bullet-proof (or idiot-proof for that matter :P)
 */
fun formatFlagTime(ms: Long): String {
    val allSeconds = ms / 1000
    val seconds: Long = allSeconds % 60
    val minutes: Long = (allSeconds / 60) % 60
    val hours: Long = allSeconds / (60*60)
    return listOf(hours, minutes, seconds)
            .joinToString(":") { it.toString().padStart(2, '0') }
}

/**
 * RecordingFlag Adapter which binds data from the RecordingFlag class into the layout XML
 */
class RecordingFlagAdapter : ArrayAdapter<RecordingFlag> {
    constructor(context: Context, items: ArrayList<RecordingFlag>)
            : super(context, 0, items) { }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?:
            LayoutInflater.from(context).inflate(R.layout.item_flag, parent, false)
        val flag = getItem(position)
        view.apply {
            text_label.setText(flag.label)
            text_time.text = formatFlagTime(flag.time)
            image_flag.setColorFilter(flag.color)
            text_label.background.setTint(flag.color)
        }
        return view
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
 * Map of button id -> color id; this is because Android won't let me get the tint color of the
 * button from the code -_-
 */
val FLAG_BUTTON_COLORS = mapOf(
        R.id.flag1 to R.color.flag_red,
        R.id.flag2 to R.color.flag_yellow,
        R.id.flag3 to R.color.flag_green,
        R.id.flag4 to R.color.flag_blue,
        R.id.flag5 to R.color.flag_purple
)

/**
 * The activity class for recording a new... recording
 */
class RecordingActivity : AppCompatActivity() {
    private lateinit var flagsAdapter : RecordingFlagAdapter
    private var recordingStart : Long = System.currentTimeMillis()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recording)

        // dummy data
        val data = arrayListOf(
                RecordingFlag(1324, colorFromId(R.color.flag_red), null),
                RecordingFlag(7500, colorFromId(R.color.flag_blue), "This is where Dale starting laughing again + more words to make this longer"),
                RecordingFlag(18000, colorFromId(R.color.flag_red), null)
        )

        flagsAdapter = RecordingFlagAdapter(this, data)
        list_flags.adapter = flagsAdapter

        timer("Recording timer",
                period=1000,
                action={
                    text_timer.post {
                        val time = System.currentTimeMillis() - recordingStart
                        text_timer.text = formatFlagTime(time)
                    }
                }
        )
    }

    fun flagButtonPressed(view: View) {
        val colorId = FLAG_BUTTON_COLORS[view.id]
        val time = System.currentTimeMillis() - recordingStart
        flagsAdapter.add(RecordingFlag(time, colorFromId(colorId!!), null))
    }
}
