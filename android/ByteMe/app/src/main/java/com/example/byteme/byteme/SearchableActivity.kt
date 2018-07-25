package com.example.byteme.byteme

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import helpers.businessLayer.InnerRecordingMatch
import helpers.businessLayer.RecordingMatch
import helpers.businessLayer.SoundSearchManager
import kotlinx.android.synthetic.main.item_recording_match.view.*
import kotlinx.android.synthetic.main.item_recording_search_result.view.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import java.text.SimpleDateFormat
import java.util.*

class SearchableActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: SearchAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager

    // Empty list to hold matches
    private var searchMatches: MutableList<RecordingMatch> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        viewManager = LinearLayoutManager(this)
        // keyword is temporary for now; populated when intent comes in
        viewAdapter = SearchAdapter(this, "transcription match", searchMatches)

        recyclerView = findViewById<RecyclerView>(R.id.rv_search_list).apply {
            // changes in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)

            // linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter
            adapter = viewAdapter
        }

        launch {
            handleIntent(intent)

            // Let the viewAdapter know its content needs to be updated
            launch(UI) {viewAdapter.notifyDataSetChanged()}
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        launch{
            handleIntent(intent)
            launch(UI) {viewAdapter.notifyDataSetChanged()}
        }
    }

    suspend fun handleIntent(intent: Intent) {
        // Verify intent is from search dialog, i.e. is the right action, and get the query
        if (Intent.ACTION_SEARCH == intent.action) {
            val query = intent.getStringExtra(SearchManager.QUERY)
            val appData = intent.getBundleExtra(SearchManager.APP_DATA)
            var id: Long? = null
            if (appData != null) {
                id = appData.getLong("recording_id")
            }

            Log.d("debug", id.toString())

            // Perform the search
            val searchManager = SoundSearchManager(this@SearchableActivity)
            viewAdapter.keyword = query

            // Populate searchMatches with search matches returned from search
            searchMatches.addAll(searchManager.search(query, id))
        }
    }

    private class SearchAdapter(
            private val context: Context,
            var keyword: String,
            private val searchMatches: MutableList<RecordingMatch>) :
            RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder.
        // Each data item is just a string in this case that is shown in a TextView.
        // class ViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)

        class ViewHolder(view: View, context: Context, keyword: String) : RecyclerView.ViewHolder(view) {
            // Holds the view that will add each recording title
            val container = view.list_item_layout
            val tvRecordingTitle = view.tv_recording_title
            val tvDate = view.tv_date
            val tvDuration = view.tv_duration
            val lvMatches = view.lv_matches
        }

        // Create new views (invoked by the layout manager)
        override fun onCreateViewHolder(parent: ViewGroup,
                                        viewType: Int): SearchAdapter.ViewHolder {

            // create a new view
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_recording_search_result, parent, false)
            return ViewHolder(view, context, keyword)
        }

        // Replace the contents of a view (invoked by the layout manager)
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            val match = searchMatches[position]
            holder.container.tag = match.recording.id
            holder.tvRecordingTitle.text = match.recording.name
            holder.tvDate.text = DATE_FORMAT.format(match.recording.created)
            holder.tvDuration.text = helpers.timeToString(match.recording.duration)

            val adapter = InnerRecordingMatchAdapter(context, arrayListOf(), keyword)
            adapter.addAll(match.matches)
            holder.lvMatches.adapter = adapter
            adapter.notifyDataSetChanged()
        }

        // Return the number of recordings in the list
        override fun getItemCount() = searchMatches.size
    }

    /**
     * Adapter which binds data from the InnerRecordingMatch class into the layout XML
     */
    class InnerRecordingMatchAdapter(
            context: Context,
            items: ArrayList<InnerRecordingMatch>,
            val keyword: String
    ) : ArrayAdapter<InnerRecordingMatch>(context, 0, items) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view = convertView ?:
            LayoutInflater.from(context).inflate(R.layout.item_recording_match, parent, false)
            val match = getItem(position)
            view.apply {
                when(match) {
                    is InnerRecordingMatch.Companion.Flag -> {
                        text_label.text = match.flag.label
                        image_flag.setColorFilter(match.flag.color)
                        image_flag.visibility = View.VISIBLE
                        image_transcript.visibility = View.GONE
                    }
                    is InnerRecordingMatch.Companion.Transcript -> {
                        text_label.text = keyword
                        image_flag.visibility = View.GONE
                        image_transcript.visibility = View.VISIBLE
                    }
                }
                text_time.text = helpers.timeToString(match.timestamp)

                setOnClickListener {
                    val intent = Intent(context, PlaybackActivity::class.java)
                    intent.putExtra("recording_id", match.recordingId)
                    intent.putExtra("timestamp", match.timestamp)
                    context.startActivity(intent)
                }
            }
            return view
        }
    }

    fun openRecording(view: View) {
        val intent = Intent(this, PlaybackActivity::class.java)

        // To pass any data to next activity
        intent.putExtra("recording_id", view.tag as Long)

        // start your next activity
        startActivity(intent)
    }

    companion object {
        private val DATE_FORMAT = SimpleDateFormat("yyyy/MM/dd", Locale.CANADA)
    }
}
