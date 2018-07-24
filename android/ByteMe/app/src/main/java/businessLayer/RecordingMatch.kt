package helpers.businessLayer

import businessLayer.RecordingRoom

data class RecordingMatch(
        val recording: RecordingRoom,
        val matches: List<InnerRecordingMatch>
)