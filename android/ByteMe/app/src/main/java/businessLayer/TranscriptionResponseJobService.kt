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

class TranscriptionResponseJobService : JobService() {

    override fun onStartJob(params: JobParameters): Boolean {
        Log.d(TAG, "Job started")

        val recordingId = params.extras.getLong("recording_id")
        val operationName = params.extras.getString("operation_name")
        launch {
            val result = SpeechClient.getOperation(operationName)

            if (result == null) {
                Log.d(TAG, "Not ready")
                jobFinished(params, true)
            } else {
                Log.d(TAG, "Success!")

                val db = AppDatabase.getInstance(this@TranscriptionResponseJobService)!!
                db.recordingDao.addTranscript(recordingId, result.transcript)
                // Remove first in case the job had previously failed
                db.transcriptWordsDao.removeWordsForRecording(recordingId)
                db.transcriptWordsDao.insert(result.toTranscriptWords(recordingId))
                jobFinished(params, false)
            }
        }

        return true // returning true signifies async task
    }

    override fun onStopJob(params: JobParameters): Boolean {
        // Called if job is cancelled
        Log.d(TAG, "Job cancelled before completion!")
        return true // reschedule job
    }

    companion object {
        private const val TAG = "TranscriptionResponseJS"

        fun scheduleResponseListener(context: Context, recordingId: Long, operationName: String) {
            // jobIds must be int, but the recording ID is Long; use the hash to reduce
            // likelihood of collisions
            val jobId = recordingId.hashCode()
            val scheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
            val componentName = ComponentName(context, TranscriptionResponseJobService::class.java)
            val jobInfo = JobInfo.Builder(jobId, componentName)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setMinimumLatency(5_000)
                    .setExtras(PersistableBundle().apply {
                        putLong("recording_id", recordingId)
                        putString("operation_name", operationName)
                    })
                    .build()

            scheduler.schedule(jobInfo)
        }
    }
}