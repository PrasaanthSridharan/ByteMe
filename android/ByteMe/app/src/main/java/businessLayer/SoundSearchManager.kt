package helpers.businessLayer

import android.content.Context
import businessLayer.RecordingFlagRoom
import businessLayer.RecordingRoom
import dataAccessLayer.AppDatabase

class SoundSearchManager(val context: Context) {

    suspend fun search(keyword: String): Array<RecordingMatch> {
        val db = AppDatabase.getDummyInstance(context)!!

        val flagResults = db.recordingFlagDao.search("%" + keyword + "%")

        val recordings = db.recordingDao.searchTranscripts("%" + keyword + "%")
        val transcriptWords = mutableListOf<TranscriptWordsRoom>()

        for (recording in recordings) {
            if (recording.transcript != null) {
                for ((index, word) in recording.transcript.split(" ").withIndex()) {
                    if (word.equals(keyword)) {
                        transcriptWords.addAll(db.transcriptWordsDao.search(recording.id!!, index))
                    }
                }
            }
        }

        return resultToArray(db, flagResults, transcriptWords)
    }

    suspend fun search(keyword: String, recording: RecordingRoom): Array<RecordingMatch> {
        val db = AppDatabase.getDummyInstance(context)!!

        val flagResults = db.recordingFlagDao.searchRecording(recording.id!!, "%" + keyword + "%")

        val transcriptWords = mutableListOf<TranscriptWordsRoom>()

        if (recording.transcript != null) {
            for ((index, word) in recording.transcript.split(" ").withIndex()) {
                if (word.equals(keyword)) {
                    transcriptWords.addAll(db.transcriptWordsDao.search(recording.id!!, index))
                }
            }
        }

        return resultToArray(db, flagResults, transcriptWords)
    }

    fun resultToArray(db: AppDatabase, flagResults: List<RecordingFlagRoom>, transcriptWords: List<TranscriptWordsRoom>): Array<RecordingMatch>  {
        var resultList = mutableListOf<RecordingMatch>()

        for (result in flagResults) {
            val recording = db.recordingDao.get(result.recordingId)
            resultList.add(RecordingMatch(recording, result.time))
        }

        for (result in transcriptWords) {
            val recording = db.recordingDao.get(result.recordingId)
            resultList.add(RecordingMatch(recording, result.audioOffset))
        }

        return resultList.toTypedArray()
    }

}