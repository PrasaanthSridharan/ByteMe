package speechClient

import org.json.JSONObject

data class WordInfo(
        /** Start of the word in ms */
        val start: Long,
        /** End of the word in ms */
        val end: Long,
        val word: String
) {
    constructor(json: JSONObject): this(
            start = parseOffset(json.getString("startTime")),
            end = parseOffset(json.getString("endTime")),
            word = json.getString("word")
    )
}

private fun parseOffset(str: String): Long
        = (str.dropLast(1).toFloat() * 1000).toLong()