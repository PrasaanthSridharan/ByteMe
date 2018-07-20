package com.example.byteme.byteme

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings.System.DATE_FORMAT
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import businessLayer.RecordingRoom
import dataAccessLayer.AppDatabase
import kotlinx.android.synthetic.main.recording_list_item.view.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

class SearchActivity : AppCompatActivity() {
    private lateinit var db: AppDatabase
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    private var myDataset: MutableList<RecordingRoom> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        viewManager = LinearLayoutManager(this)
        viewAdapter = SearchAdapter(myDataset)

        recyclerView = findViewById<RecyclerView>(R.id.rv_search_list).apply {
            // changes in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)

            // linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter
            adapter = viewAdapter
        }

        launch {
//            db = AppDatabase.getInstance(this@MainActivity)!!
            db = AppDatabase.getDummyInstance(this@SearchActivity)!!

            myDataset.addAll(db.recordingDao.getAll())
            launch(UI) { viewAdapter.notifyDataSetChanged() }
        }
    }

    private class SearchAdapter(private val myDataset: MutableList<RecordingRoom>) :
            RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

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
                                        viewType: Int): SearchAdapter.ViewHolder {
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
}
