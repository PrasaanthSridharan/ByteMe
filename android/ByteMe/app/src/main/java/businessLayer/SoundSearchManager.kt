package helpers.businessLayer

import android.content.Context
import businessLayer.RecordingRoom
import dataAccessLayer.AppDatabase

class SoundSearchManager(val context: Context) {

    suspend fun search(keyword: String, recordingId: Long? = null): List<RecordingMatch> {
        val db = AppDatabase.getInstance(context)!!

        // These are all the recordings we'll return
        val matchingRecordingIds = mutableMapOf<Long, RecordingRoom?>()

        // Matches in recording name
        if (recordingId == null) {
            val recordingNameMatches = db.recordingDao.searchNames("%$keyword%")
            recordingNameMatches.forEach { matchingRecordingIds[it.id!!] = it }
        }

        // Matches in flag keywords
        val flagMatches = db.recordingFlagDao.search("%$keyword%", recordingId)
                .map { InnerRecordingMatch.Companion.Flag(it) }
        flagMatches.forEach {
            if (!matchingRecordingIds.containsKey(it.recordingId))
                matchingRecordingIds[it.recordingId] = null
        }

        // Matches in transcript
        val transcriptMatches = when(recordingId) {
            null -> db.recordingDao.searchTranscripts("%$keyword%")
            else -> listOf(db.recordingDao.get(recordingId))
        }
        val transcriptWords = mutableListOf<TranscriptWordsRoom>()
        transcriptMatches.forEach { matchingRecordingIds[it.id!!] = it }

        for (recording in transcriptMatches) {
            for ((index, word) in recording.transcript!!.split(" ").withIndex()) {
                if (word == keyword) {
                    transcriptWords.addAll(db.transcriptWordsDao.search(recording.id!!, index))
                }
            }
        }

        val transcriptWordMatches = transcriptWords
                .map { InnerRecordingMatch.Companion.Transcript(it) }

        val innerMatches = flagMatches + transcriptWordMatches

        // Load remaining recordings
        val idsToGet = matchingRecordingIds
                .filterValues { it == null }
                .keys
        db.recordingDao.getMany(idsToGet)
                .forEach { matchingRecordingIds[it.id!!] = it }

        val innerMatchesByRecording = innerMatches
                .groupBy { it.recordingId }
                .mapValues { (_, matches) -> matches.sortedBy { it.timestamp } }

        return matchingRecordingIds.values.toList().map { recording ->
            RecordingMatch(recording!!, innerMatchesByRecording[recording.id] ?: listOf())
        }
    }
}