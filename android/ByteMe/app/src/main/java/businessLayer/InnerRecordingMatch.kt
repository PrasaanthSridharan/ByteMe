package helpers.businessLayer

import businessLayer.RecordingFlagRoom

abstract class InnerRecordingMatch(
    val recordingId: Long,
    val timestamp: Long
) {
    companion object {
        data class Transcript(
                val transcriptWords: TranscriptWordsRoom
        ) : InnerRecordingMatch(transcriptWords.recordingId, transcriptWords.audioOffset)

        data class Flag(
                val flag: RecordingFlagRoom
        ) : InnerRecordingMatch(flag.recordingId, flag.time)
    }
}