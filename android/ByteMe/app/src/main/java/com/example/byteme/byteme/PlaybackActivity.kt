package com.example.byteme.byteme

import businessLayer.*

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.media.MediaPlayer
import android.net.Uri
import android.os.Environment
import android.os.Handler
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import helpers.colorFromId
import kotlinx.android.synthetic.main.playback_bookmarks_list.view.*


val audioFile = "/SmartVoiceRecorder/Record_0001.wav"

class PlaybackActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playback)

        val musicData = Uri.parse (Environment.getExternalStorageDirectory().getPath ()
                + audioFile)
        val mp = MediaPlayer.create (this, musicData)

        setupAudio(mp)
        setupBookmarks(mp)
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

        val data = arrayOf(RecordingFlag(10000, colorFromId(R.color.flag_red), "flag1"),
                RecordingFlag(23000, colorFromId(R.color.flag_green), "flag2"),
                RecordingFlag(29000, colorFromId(R.color.flag_purple), "flag3"))

        viewAdapter = PlaybackAdapter(data, fun (time) {
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

class PlaybackAdapter(private val myDataset: Array<RecordingFlag>,
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

        holder.playButton.setOnClickListener({
            onBookmarkPlayPress(myDataset[position].time)
        })
    }

    // Return the number of recordings in the list
    override fun getItemCount() = myDataset.size
}
