package helpers.businessLayer

import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Environment
import android.util.Log
import java.io.File
import java.util.*


object RecordingManager {
    private const val TAG = "RecordingManager"
    private const val FILE_EXTENSION: String = "3gp"

    private var mediaRecorder: MediaRecorder? = null
    private var mediaPlayer: MediaPlayer? = null

    private var audioFilePath: String? = null
    private var audioFileDir: String? = null
    private var isRecording = false




    /**
     * Initializes the manager. Call before using.
     */
    fun init() {
        val externalDirectory = Environment.getExternalStorageDirectory().path
        audioFileDir = File(externalDirectory, "sound_hunt").path

        if(!File(audioFileDir).isDirectory) File(audioFileDir).mkdir()
    }

    /**
     * Starts the audio record process to output to file specified by [audioName] without a file
     * extension.
     */
    fun recordAudio( ):String {
        val audioName = UUID.randomUUID().toString()
        audioFilePath = File(audioFileDir, "$audioName.$FILE_EXTENSION").toString()

        isRecording = true

        try {
            mediaRecorder = MediaRecorder()!!.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                setOutputFile(audioFilePath)
                prepare()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d(TAG, "Recording error!")
        }

        mediaRecorder?.start()
        Log.d(TAG, "Recording now...")

        return audioFilePath!!
    }

    /**
     * Used to both stop [recordAudio] and [playAudio] depending on [isRecording] status.
     */
    fun stopAudio() {

        if (isRecording) {
            mediaRecorder?.stop()
            mediaRecorder?.release()
            mediaRecorder = null
            isRecording = false
            Log.d(TAG, "Recording stopped.")
        } else {
            mediaPlayer?.release()
            mediaPlayer = null
            Log.d(TAG, "Playback stopped.")
        }
    }

    /**
     * Plays the audio when specified the [fileNameWithExtension] of the audio file to play.
     */
    fun playAudio(fileNameWithExtension:String) {
        val fullFilePath = File(audioFileDir, fileNameWithExtension).toString()

        Log.d(TAG, "fileNameWithExtension: $fullFilePath")
        Log.d(TAG, "audioFileDir: $audioFileDir")
        mediaPlayer = MediaPlayer()
        mediaPlayer?.setDataSource(File(audioFileDir, fileNameWithExtension).toString())
        mediaPlayer?.prepare()
        mediaPlayer?.start()
    }

}


