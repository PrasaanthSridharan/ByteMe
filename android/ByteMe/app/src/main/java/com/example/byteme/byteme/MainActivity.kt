package com.example.byteme.byteme

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.activity_recording.view.*
import kotlinx.android.synthetic.main.recording_list_item.view.*

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    // dataset of dummy recording titles
    val myDataset = arrayOf("Meeting", "Fri_scrum", "Mon_scrum", "Lecture", "Interview",
            "Approval", "Brainstorming")

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
    }

    class RecordingAdapter(private val myDataset: Array<String>) :
            RecyclerView.Adapter<RecordingAdapter.ViewHolder>() {

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder.
        // Each data item is just a string in this case that is shown in a TextView.
        //class ViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)

        class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
            // Holds the TextView that will add each animal to
            val tvRecordingTitle = view.tv_recording_title
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
            holder.tvRecordingTitle.text = myDataset[position]
        }

        // Return the number of recordings in the list
        override fun getItemCount() = myDataset.size
    }

    class Recording(val title: String, val date: String, val timestamp: Int) {
        // To do: change type of date property to actual date type
    }
}
