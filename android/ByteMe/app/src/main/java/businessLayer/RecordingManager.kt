package helpers.businessLayer

import android.media.AudioFormat
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Environment
import android.util.Log
import omrecorder.*
import java.io.File
import java.util.*


object RecordingManager {
    private const val TAG = "RecordingManager"
    private const val FILE_EXTENSION: String = "wav"

    private var recorder: Recorder? = null
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
     * Starts the audio record process to a new (random) file in the [audioFileDir]
     */
    fun recordAudio(): String {
        val audioName = UUID.randomUUID().toString()
        val audioFile = File(audioFileDir, "$audioName.$FILE_EXTENSION")
        audioFilePath = audioFile.path

        isRecording = true

        val audioSource = PullableSource.Default(
            AudioRecordConfig.Default(
                    MediaRecorder.AudioSource.MIC,
                    AudioFormat.ENCODING_PCM_16BIT,
                    AudioFormat.CHANNEL_IN_MONO,
                    16000
            )
        )

        Log.d(TAG, "Recording now...")
        recorder = OmRecorder.wav(PullTransport.Default(audioSource), audioFile).apply {
            startRecording()
        }

        return audioFile.path
    }

    /**
     * Used to both stop [recordAudio] and [playAudio] depending on [isRecording] status.
     */
    fun stopAudio() {

        if (isRecording) {
            recorder!!.stopRecording()
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


