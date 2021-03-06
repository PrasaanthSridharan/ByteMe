package businessLayer

import android.app.job.JobInfo
import android.app.job.JobParameters
import android.app.job.JobScheduler
import android.app.job.JobService
import android.content.ComponentName
import android.content.Context
import android.os.PersistableBundle
import android.util.Log
import kotlinx.coroutines.experimental.launch
import speechClient.SpeechClient

private const val TAG = "TranscriptionJobService"

class TranscriptionJobService : JobService() {
    override fun onStartJob(params: JobParameters): Boolean {
        Log.d(TAG, "Job started")

        val recordingId = params.extras.getLong("recording_id")
        val audioFile = params.extras.getString("audio_file")
        launch {
            val opName = SpeechClient.recognizeLongForm(audioFile)
            if (opName == null) {
                Log.e(TAG, "We didn't get an operation name back...")
                jobFinished(params, false)
            }
            else {
                TranscriptionResponseJobService.scheduleResponseListener(
                        applicationContext,
                        recordingId,
                        opName)
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