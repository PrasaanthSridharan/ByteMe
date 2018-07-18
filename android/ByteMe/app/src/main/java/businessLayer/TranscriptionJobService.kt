package businessLayer

import android.app.job.JobInfo
import android.app.job.JobParameters
import android.app.job.JobScheduler
import android.app.job.JobService
import android.content.ComponentName
import android.content.Context
import android.os.PersistableBundle
import android.util.Log
import dataAccessLayer.AppDatabase
import kotlinx.coroutines.experimental.launch
import speechClient.SpeechClient
import speechClient.SpeechRecognitionResult

private const val TAG = "TranscriptionJobService"

class TranscriptionJobService : JobService() {
    override fun onStartJob(params: JobParameters): Boolean {
        Log.d(TAG, "Job started")

        val recordingId = params.extras.getLong("recording_id")
        val audioFile = params.extras.getString("audio_file")
        launch {
            val result = SpeechClient.recognize(audioFile)

            val db = AppDatabase.getDummyInstance(this@TranscriptionJobService)!!
            db.recordingDao.addTranscript(recordingId, result.transcript)
            // Do this in case the job had previously failed
            db.transcriptWordsDao.removeWordsForRecording(recordingId)
            db.transcriptWordsDao.insertTranscription(recordingId, TranscriptWords(result))
            jobFinished(params, false)
        }

        return true; // returning true signifies async task
    }

    override fun onStopJob(params: JobParameters): Boolean {
        // Called if job is cancelled
        Log.d(TAG, "Job cancelled before completion!")
        return true // reschedule job
    }

    companion object {
        fun scheduleTranscriptionJob(context: Context, recordingId: Long, audioFile: String) {
            // jobIds must be int, but the recording ID is Long; use the hash to reduce
            // likelihood of collisions
            val jobId = recordingId.hashCode()
            val scheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
            val componentName = ComponentName(context, TranscriptionJobService::class.java)
            val jobInfo = JobInfo.Builder(jobId, componentName)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setExtras(PersistableBundle().apply {
                        putLong("recording_id", recordingId)
                        putString("audio_file", audioFile)
                    })
                    .build()

            scheduler.schedule(jobInfo)
        }
    }
}

@Deprecated("Will be replaced by an @Entity data class")
private data class TranscriptWords(
        val wordIndices: Array<Int>,
        val timeOffsets: List<Long>
) {
    constructor(recResult: SpeechRecognitionResult): this(
            wordIndices = getWordIndices(recResult),
            timeOffsets = recResult.words.map { it.start }
    )

    companion object {
        private fun getWordIndices(recResult: SpeechRecognitionResult): Array<Int> {
            var curIndex = 0
            return Array(recResult.words.size) { i ->
                val wordInfo = recResult.words[i]
                val wordStartIndex = recResult.transcript.indexOf(wordInfo.word, curIndex)
                curIndex = wordStartIndex + wordInfo.word.length
                wordStartIndex
            }
        }
    }
}

@Deprecated("Will be replaced by an @Dao abstract class")
private object TranscriptWordsDao {
    fun insertTranscription(recordingId: Long, transcriptWords: TranscriptWords) {
        Log.d(TAG, "Inserting into TranscriptionWordsDao: $transcriptWords")
    }

    fun removeWordsForRecording(recordingId: Long) {
        Log.d(TAG, "Removing words for $recordingId")
    }
}

@Deprecated("Will be replaced by actual implementation")
private val AppDatabase.transcriptWordsDao: TranscriptWordsDao
    get() = TranscriptWordsDao