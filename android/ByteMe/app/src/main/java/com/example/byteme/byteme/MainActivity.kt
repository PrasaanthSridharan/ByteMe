package com.example.byteme.byteme

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import businessLayer.RecordingRoom
import dataAccessLayer.AppDatabase
import kotlinx.android.synthetic.main.recording_list_item.view.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import java.text.SimpleDateFormat


class MainActivity : AppCompatActivity() {
    private lateinit var db: AppDatabase
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    private val RECORD_REQUEST_CODE = 101
    private val STORAGE_REQUEST_CODE = 102

    // dataset of dummy recording titles
    /*
    val myDataset = arrayOf("Meeting", "Fri_scrum", "Mon_scrum", "Lecture", "Interview",
            "Approval", "Brainstorming")
    */

    private var myDataset: MutableList<RecordingRoom> = mutableListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // TODO: Fix me, this isn't fully correct ---
        requestPermission(Manifest.permission.RECORD_AUDIO,
                RECORD_REQUEST_CODE)
        requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                STORAGE_REQUEST_CODE)
        // TODO: fix me ends here ---


        viewManager = LinearLayoutManager(this)
        //rv_recording_list.layoutManager = LinearLayoutManager(this)

        viewAdapter = RecordingAdapter(myDataset)

        recyclerView = findViewById<RecyclerView>(R.id.rv_recording_list).apply {
            // changes in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)

            // linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter
            adapter = viewAdapter
        }

        launch {
            db = AppDatabase.getInstance(this@MainActivity)!!

            myDataset.addAll(db.recordingDao.getAll())
            launch(UI) { viewAdapter.notifyDataSetChanged() }
        }

        val micButton = findViewById<ImageButton>(R.id.mic_button)

        micButton.setOnClickListener {
            val intent = Intent(this, RecordingActivity::class.java)
            startActivity(intent)
        }
    }

    private fun requestPermission(permissionType: String, requestCode: Int) {
        val permission = ContextCompat.checkSelfPermission(this,
                permissionType)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(permissionType), requestCode
            )
        }
    }

    private class RecordingAdapter(private val myDataset: MutableList<RecordingRoom>) :
            RecyclerView.Adapter<RecordingAdapter.ViewHolder>() {

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder.
        // Each data item is just a string in this case that is shown in a TextView.
        //class ViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            // Holds the view that will add each recording title
            val container = view.linearLayout
            val tvRecordingTitle = view.tv_recording_title
            val tvDate = view.tv_date
            val tvDuration = view.tv_duration
        }

        // Create new views (invoked by the layout manager)
        override fun onCreateViewHolder(parent: ViewGroup,
                                        viewType: Int): RecordingAdapter.ViewHolder {
            // create a new view
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recording_list_item, parent, false)
            return ViewHolder(view)
        }

        // Replace the contents of a view (invoked by the layout manager)
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            val recording = myDataset[position]
            holder.container.tag = recording.id
            holder.tvRecordingTitle.text = recording.name
            holder.tvDate.text = DATE_FORMAT.format(recording.created)
            holder.tvDuration.text = helpers.timeToString(recording.duration)
        }

        // Return the number of recordings in the list
        override fun getItemCount() = myDataset.size
    }

    fun openRecording(view: View) {
        val intent = Intent(this, PlaybackActivity::class.java)
        // To pass any data to next activity
        intent.putExtra("recording_id", view.tag as Long)
        // start your next activity
        startActivity(intent)
    }

    companion object {
        private val DATE_FORMAT = SimpleDateFormat("yyyy/MM/dd")
    }
}

