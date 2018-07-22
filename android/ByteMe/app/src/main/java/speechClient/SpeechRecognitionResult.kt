package speechClient

import helpers.businessLayer.TranscriptWordsRoom
import org.json.JSONArray
import org.json.JSONObject

data class SpeechRecognitionResult(
        val transcript: String,
        val confidence: Double,
        val words: ArrayList<WordInfo>
) {
    constructor(json: JSONObject): this(
            transcript = json.getString("transcript"),
            confidence = json.getDouble("confidence"),
            words = json.getJSONArray("words").map<JSONObject, WordInfo>{ WordInfo(it) }
    )

    fun toTranscriptWords(recordingId: Long): Collection<TranscriptWordsRoom> {
        val result = mutableListOf<TranscriptWordsRoom>()

        for ((index, word) in words.withIndex()) {
            result.add(TranscriptWordsRoom(id = null,
                    recordingId = recordingId,
                    transcriptIndex = index,
                    audioOffset = word.start))
        }

        return result
    }
}

/**
 * Kotlin's map function adapted for JSONArray
 */
private inline fun <T, R> JSONArray.map(transform: (T) -> R): ArrayList<R> {
    return ArrayList<R>(length()).apply {
        for (i in 0 until this@map.length())
            add(transform(this@map[i] as T))
    }
}