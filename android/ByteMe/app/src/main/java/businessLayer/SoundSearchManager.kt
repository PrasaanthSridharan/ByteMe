package helpers.businessLayer

import android.content.Context
import businessLayer.RecordingFlagRoom
import businessLayer.RecordingRoom
import dataAccessLayer.AppDatabase

class SoundSearchManager(val context: Context) {

    suspend fun search(keyword: String): Array<RecordingMatch> {
        val db = AppDatabase.getDummyInstance(context)!!

        val flagResults = db.recordingFlagDao.search("%" + keyword + "%")

        return resultToArray(db, flagResults)
    }

    suspend fun search(keyword: String, recording: RecordingRoom): Array<RecordingMatch> {
        val db = AppDatabase.getDummyInstance(context)!!

        val flagResults = db.recordingFlagDao.searchRecording(recording.id!!, "%" + keyword + "%")

        return resultToArray(db, flagResults)
    }

    fun resultToArray(db: AppDatabase, flagResults: List<RecordingFlagRoom>): Array<RecordingMatch>  {
        var resultList = mutableListOf<RecordingMatch>()

        for (result in flagResults) {
            val recording = db.recordingDao.get(result.recordingId)
            resultList.add(RecordingMatch(recording, result.time))
        }

        return resultList.toTypedArray()
    }

}