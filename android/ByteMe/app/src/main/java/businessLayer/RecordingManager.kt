package helpers.businessLayer

import android.media.MediaPlayer
import android.media.MediaRecorder
import android.util.Log
import java.io.File
import java.util.*


object RecordingManager {

    private const val ROOT_PATH: String = "/storage/emulated/0/"
    private const val FILE_EXTENSION: String = "3gp"

    private var mediaRecorder: MediaRecorder? = null
    private var mediaPlayer: MediaPlayer? = null

    private var audioFilePath: String? = null
    private var audioFileDir: String? = null
    private var isRecording = false


    /**
     * Initializes the //TODO: Finish documentation here
     * Sets null to following instances: [mediaPlayer], [mediaRecorder], [audioFilePath]
     * Sets false to following instances: [isRecording]
     */
    fun init(fileDirectory: String) {
        mediaPlayer = null
        mediaRecorder = null
        audioFilePath = null
        isRecording = false

        audioFileDir = ROOT_PATH
    }

    /**
     * Starts the audio record process to output to file specified by [audioName] without a file
     * extension.
     */
    fun recordAudio( ):String {
        val audioName = UUID.randomUUID().toString()
        audioFilePath = File(ROOT_PATH, "$audioName.$FILE_EXTENSION").toString()

        // Todo: Refactor Start ---
       Log.d("FILE", audioFilePath)
        // Todo: Refactor End -----

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
            Log.e("recordAudio(...)", "Recording error!")
        }

        mediaRecorder?.start()
        Log.e("recordAudio(...)", "Recording now...")

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
            Log.e("stopAudio()", "Recording stopped.")
        } else {
            mediaPlayer?.release()
            mediaPlayer = null
            Log.e("stopAudio()", "Playback stopped.")
        }
    }

    /**
     * Plays the audio when specified the [fileNameWithExtension] of the audio file to play.
     */
    fun playAudio(fileNameWithExtension:String) {
        val fullFilePath = File(audioFileDir, fileNameWithExtension).toString()

        Log.e("playAudio(...)", "fileNameWithExtension: $fullFilePath")
        Log.e("playAudio(...)", "audioFileDir: $audioFileDir")
        mediaPlayer = MediaPlayer()
        mediaPlayer?.setDataSource(File(audioFileDir, fileNameWithExtension).toString())
        mediaPlayer?.prepare()
        mediaPlayer?.start()
    }

}


