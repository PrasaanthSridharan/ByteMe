package com.example.byteme.byteme

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.support.annotation.ColorInt
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import businessLayer.RecordingFlagRoom
import businessLayer.RecordingRoom
import businessLayer.TranscriptionJobService
import dataAccessLayer.AppDatabase
import helpers.businessLayer.RecordingManager
import helpers.colorFromId
import kotlinx.android.synthetic.main.activity_recording.*
import kotlinx.android.synthetic.main.item_flag.view.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import java.util.*
import kotlin.concurrent.timer


/**
 * RecordingFlag Adapter which binds data from the RecordingFlag class into the layout XML
 */
class RecordingFlagAdapter : ArrayAdapter<RecordingFlagModel> {
    constructor(context: Context, items: ArrayList<RecordingFlagModel>)
            : super(context, 0, items) { }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?:
            LayoutInflater.from(context).inflate(R.layout.item_flag, parent, false)
        val flag = getItem(position)
        view.apply {
            text_label.setText(flag.label)
            text_time.text = helpers.timeToString(flag.time)
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

data class RecordingModel(
        var name: String,
        var created: Date,
        var path: String)

data class RecordingFlagModel(
        val time: Long, // ms from start of recording
        @ColorInt val color: Int,
        var label: String?)

/**
 * The activity class for recording a new... recording
 */
class RecordingActivity : AppCompatActivity() {
    private lateinit var flagsAdapter : RecordingFlagAdapter

    // UI-relevant fields of the recording
    private var model = RecordingModel(name = "", created = Date(),
            path = AppDatabase.DUMMY_AUDIO_FILE)

    private var recordingStart : Long = System.currentTimeMillis()
    private var flags : ArrayList<RecordingFlagModel> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recording)

        flagsAdapter = RecordingFlagAdapter(this, flags)
        list_flags.adapter = flagsAdapter

        RecordingManager.init(Environment.getExternalStorageDirectory().absolutePath)
        model.path = RecordingManager.recordAudio()

        launch {
            val db = AppDatabase.getDummyInstance(this@RecordingActivity)!!
            model.name = db.recordingDao.getNextRecordingName()
            launch(UI) { text_title.setText(model.name) }
        }

        // Sync recording title
        text_title.onFocusChangeListener = View.OnFocusChangeListener {
            _, hasFocus -> if (!hasFocus) model.name = text_title.text.toString()
        }

        timer("Recording timer", period=1000) {
            text_timer.post {
                val time = System.currentTimeMillis() - recordingStart
                text_timer.text = helpers.timeToString(time)
            }
        }
    }

    fun flagButtonPressed(view: View) {
        val colorId = FLAG_BUTTON_COLORS[view.id]
        val time = System.currentTimeMillis() - recordingStart
        flagsAdapter.add(RecordingFlagModel(time, colorFromId(colorId!!), null))
    }

    fun stopButtonPressed(view: View) {
        val intent = Intent(this, MainActivity::class.java)
        // To pass any data to next activity
        //intent.putExtra("keyIdentifier", value)
        // start your next activity

        RecordingManager.stopAudio()  // Stop Recording

        launch {
            val context = this@RecordingActivity
            val db = AppDatabase.getDummyInstance(context)!!
            val recordingId = saveRecording(db)
            saveFlags(db, recordingId)
            TranscriptionJobService.scheduleTranscriptionJob(context, recordingId, model.path)

            launch (UI) { startActivity(intent) }
        }
    }

    private suspend fun saveRecording(db: AppDatabase): Long {
        val recording = RecordingRoom(
                id = null,
                path = model.path,
                name = model.name,
                created = model.created,
                transcript = null,
                duration = System.currentTimeMillis() - recordingStart
        )

        return db.recordingDao.insert(recording)
    }

    private suspend fun saveFlags(db: AppDatabase, recordingId: Long) {
        val roomFlags = flags.map { model ->
            RecordingFlagRoom(
                    id = null,
                    recordingId = recordingId,
                    label = model.label,
                    color = model.color,
                    time = model.time
            )
        }

        db.recordingFlagDao.insert(roomFlags)
    }
}
