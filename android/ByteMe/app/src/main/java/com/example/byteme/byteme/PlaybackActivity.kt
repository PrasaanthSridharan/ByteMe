package com.example.byteme.byteme

import android.app.SearchManager
import android.content.Context
import android.graphics.Typeface
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.*
import android.widget.ImageButton
import android.widget.SearchView
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import businessLayer.RecordingFlagRoom
import businessLayer.RecordingRoom
import dataAccessLayer.AppDatabase
import kotlinx.android.synthetic.main.activity_playback.*
import kotlinx.android.synthetic.main.playback_bookmarks_list.view.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch


class PlaybackActivity : AppCompatActivity() {
    private lateinit var recording: RecordingRoom
    private lateinit var flags: List<RecordingFlagRoom>

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private var recordingId: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playback)

        recordingId = this.intent.extras["recording_id"] as Long

        setupTabs()

        launch {
            val db = AppDatabase.getInstance(this@PlaybackActivity)!!
            recording = db.recordingDao.get(recordingId!!)
            flags = db.recordingFlagDao.getForRecording(recordingId!!)

            val musicData = Uri.parse(recording.path)
            val mp = MediaPlayer.create(this@PlaybackActivity, musicData)

            launch(UI) {
                setupAudio(mp)
                setupBookmarks(mp)
                setupTranscript(recording.transcript)
            }
        }
    }

    override fun onSearchRequested(): Boolean {
        val appData = Bundle()
        Log.d("debug", "poulet")
        Log.d("debug", recordingId.toString())
        appData.putLong("recording_id", recordingId!!)
        startSearch(null, false, appData, false)
        return true
    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.getItemId()) {
//            R.id.options_menu -> {
//                // start search dialog
//                super.onSearchRequested()
//                return true
//            }
//            else -> return super.onOptionsItemSelected(item)
//        }
//    }

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

    private fun setupTranscript(transcript: String?) {
        if (transcript == null)
            tv_transcript.apply {
                text = "Transcription in progress..."
                textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                setTypeface(typeface, Typeface.ITALIC)
            }
        else tv_transcript.text = transcript
    }

    private fun setupTabs() {
        tab_host.apply {
            setup()
            addTab(newTabSpec("Flags").apply {
                setContent(R.id.tab1)
                setIndicator("Flags")
            })
            addTab(newTabSpec("Transcript").apply {
                setContent(R.id.tab2)
                setIndicator("Transcript")
            })
        }
    }

    fun setupAudio(mp: MediaPlayer) {

        var playPauseButton = findViewById<ImageButton>(R.id.playback_playpause_button)
        var seekBar = findViewById<SeekBar>(R.id.playback_seekbar)

        if (mp != null) {
            playPauseButton.setOnClickListener {
                if (mp.isPlaying) {
                    mp.pause()
                    playPauseButton.setImageResource(R.drawable.ic_play_arrow_black_24dp)
                } else {
                    mp.start()
                    playPauseButton.setImageResource(R.drawable.ic_pause_black_24dp)
                }
            }
        }

        val mHandler = Handler()
        //Make sure you update Seekbar on UI thread
        this.runOnUiThread(object : Runnable {

            override fun run() {
                if (mp != null) {
                    val mCurrentPosition = mp.getCurrentPosition()
                    seekBar.setProgress(mCurrentPosition)
                }
                mHandler.postDelayed(this, 1000)
            }
        })

        seekBar.setMax(mp.duration)

        seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // TODO Auto-generated method stub
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // TODO Auto-generated method stub
            }

            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                // TODO Auto-generated method stub
                if(mp != null && fromUser){
                    mp.seekTo(progress)
                }

            }
        })

    }

    fun setupBookmarks(mp: MediaPlayer) {
        viewManager = LinearLayoutManager(this)
        //rv_recording_list.layoutManager = LinearLayoutManager(this)

        viewAdapter = PlaybackAdapter(flags, fun (time) {
            if (time <= mp.duration) {
                mp.seekTo(time.toInt())
                mp.start()
            }
        })

        recyclerView = findViewById<RecyclerView>(R.id.rv_playback_bookmarks).apply {
            // changes in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)

            // linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter
            adapter = viewAdapter
        }
    }
}

class PlaybackAdapter(private val myDataset: List<RecordingFlagRoom>,
                      private val onBookmarkPlayPress: (time: Long) -> Unit) :
        RecyclerView.Adapter<PlaybackAdapter.ViewHolder>() {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    //class ViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)

    class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        // Holds the TextView that will add each animal to
        val tvBookmarkTitle = view.tv_bookmark_title
        val tvBookmarkTime = view.tv_bookmark_time
        val playButton = view.playButton
        val flagIcon = view.flagIcon
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): PlaybackAdapter.ViewHolder {
        // create a new view
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.playback_bookmarks_list, parent, false)
        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.tvBookmarkTitle.text = myDataset[position].label
        holder.tvBookmarkTime.text = helpers.timeToString(myDataset[position].time)
        holder.flagIcon.setColorFilter(myDataset[position].color)

        holder.playButton.setOnClickListener {
            onBookmarkPlayPress(myDataset[position].time)
        }
    }

    // Return the number of recordings in the list
    override fun getItemCount() = myDataset.size
}
