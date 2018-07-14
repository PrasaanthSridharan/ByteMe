package com.example.byteme.byteme

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import businessLayer.Recording
import dataAccessLayer.AppDatabase
import kotlinx.android.synthetic.main.recording_list_item.view.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch


class MainActivity : AppCompatActivity() {
    private lateinit var db: AppDatabase
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    // dataset of dummy recording titles
    /*
    val myDataset = arrayOf("Meeting", "Fri_scrum", "Mon_scrum", "Lecture", "Interview",
            "Approval", "Brainstorming")
    */

    private var myDataset: MutableList<Recording> = mutableListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
//            db = AppDatabase.getInstance(this@MainActivity)!!
            db = AppDatabase.getDummyInstance(this@MainActivity)!!

            myDataset.addAll(db.recordingDao.getAll().map {Recording.fromRecordingRoom(it)})
            launch(UI) { viewAdapter.notifyDataSetChanged() }
        }

        val micButton = findViewById<ImageButton>(R.id.mic_button)

        micButton.setOnClickListener {
            val intent = Intent(this, RecordingActivity::class.java)
            startActivity(intent)
        }
    }

    private class RecordingAdapter(private val myDataset: MutableList<Recording>) :
            RecyclerView.Adapter<RecordingAdapter.ViewHolder>() {

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder.
        // Each data item is just a string in this case that is shown in a TextView.
        //class ViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            // Holds the view that will add each recording title
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
            holder.tvRecordingTitle.text = myDataset[position].title
            holder.tvDate.text = myDataset[position].date
            holder.tvDuration.text = myDataset[position].duration
        }

        // Return the number of recordings in the list
        override fun getItemCount() = myDataset.size
    }

    fun createDummyRecordings(num: Int) : Array<Recording>{
        //do stuff
        var recordings = Array(num){i -> Recording("Recording "+(i + 1).toString(), "2018/07/"+(i + 1).toString().padStart(2,
                    '0'), "00:02:31")}
        return recordings
    }

    fun openRecording(view: View) {
        val intent = Intent(this, PlaybackActivity::class.java)
        // To pass any data to next activity
        //intent.putExtra("keyIdentifier", value)
        // start your next activity
        startActivity(intent)
    }

}

