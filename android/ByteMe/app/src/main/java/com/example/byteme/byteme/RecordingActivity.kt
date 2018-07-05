package com.example.byteme.byteme

import businessLayer.*
import colorFromId

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import kotlin.collections.ArrayList
import kotlin.concurrent.timer


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
            text_time.text = Helper.timeToString(flag.time)
            image_flag.setColorFilter(flag.color)
            text_label.background.setTint(flag.color)
            text_label.onFocusChangeListener = View.OnFocusChangeListener {
                _, hasFocus -> if (!hasFocus) flag.label = text_label.text.toString()
            }
        }
        return view
    }
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
    private var flags : ArrayList<RecordingFlag> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recording)

        flagsAdapter = RecordingFlagAdapter(this, flags)
        list_flags.adapter = flagsAdapter

        timer("Recording timer",
                period=1000,
                action={
                    text_timer.post {
                        val time = System.currentTimeMillis() - recordingStart
                        text_timer.text = Helper.timeToString(time)
                    }
                }
        )
    }

    fun flagButtonPressed(view: View) {
        val colorId = FLAG_BUTTON_COLORS[view.id]
        val time = System.currentTimeMillis() - recordingStart
        flagsAdapter.add(RecordingFlag(time, colorFromId(colorId!!), null))
    }

    fun stopButtonPressed(view: View) {
        val intent = Intent(this, MainActivity::class.java)
        // To pass any data to next activity
        //intent.putExtra("keyIdentifier", value)
        // start your next activity
        startActivity(intent)
    }
}
