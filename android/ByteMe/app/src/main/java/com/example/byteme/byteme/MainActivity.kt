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
import android.view.*
import android.widget.ImageButton
import businessLayer.RecordingRoom
import dataAccessLayer.AppDatabase
import kotlinx.android.synthetic.main.recording_list_item.view.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import java.text.SimpleDateFormat
import android.support.v4.view.MenuItemCompat.getActionView
import android.content.Context.SEARCH_SERVICE
import android.app.SearchManager
import android.content.Context
import android.widget.SearchView


class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    private val RECORD_REQUEST_CODE = 101
    private val STORAGE_REQUEST_CODE = 102

    private var myDataset: MutableList<RecordingRoom> = mutableListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //setContentView(R.layout.activity_search)

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
            populateUI()
        }

        val micButton = findViewById<ImageButton>(R.id.mic_button)

        micButton.setOnClickListener {
            val intent = Intent(this, RecordingActivity::class.java)
            startActivity(intent)
        }
    }

    private suspend fun populateUI() {
        val db = AppDatabase.getInstance(this@MainActivity)!!

        myDataset.clear()
        myDataset.addAll(db.recordingDao.getAll())
        launch(UI) { viewAdapter.notifyDataSetChanged() }
    }

    override fun onResume() {
        super.onResume()
        launch { populateUI() }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        launch { populateUI() }
    }

    // This is the toolbar where the search icon lives
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.options_menu, menu)

        // Associate searchable configuration with the SearchView
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu.findItem(R.id.options_menu).actionView as SearchView
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(componentName))

        return true
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
            val container = view.list_item_layout
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

