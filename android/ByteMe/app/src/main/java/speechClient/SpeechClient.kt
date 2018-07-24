package speechClient

import android.util.Base64
import android.util.Log
import com.example.byteme.byteme.BuildConfig
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets

object SpeechClient {
    private const val TAG = "SpeechClient"

    private const val USE_DUMMY = true
    val DUMMY_RESULT = SpeechRecognitionResult(transcript="hello world record to text with words", confidence=0.96601915, words=arrayListOf(WordInfo(start=700, end=1300, word="hello"), WordInfo(start=1300, end=1600, word="world"), WordInfo(start=1600, end=2600, word="record"), WordInfo(start=2600, end=2800, word="to"), WordInfo(start=2800, end=3600, word="text"), WordInfo(start=3600, end=3800, word="with"), WordInfo(start=3800, end=4600, word="words")))

    fun recognize(wavFile: String, forceRequest: Boolean = false): SpeechRecognitionResult {
        return if (USE_DUMMY && !forceRequest) {
            Log.d(TAG, "Responding with dummy transcription.")
            DUMMY_RESULT
        } else {
            Log.d(TAG,"Making request transcription request for $wavFile")
            val jsonStr = googleSpeechApiRecognize(wavFile)
            Log.d(TAG, "Transcription responded: $jsonStr")
            SpeechRecognitionResult(JSONObject(jsonStr)
                    .getJSONArray("results")
                    .getJSONObject(0)
                    .getJSONArray("alternatives")
                    .getJSONObject(0)
            ).apply {
                Log.d(TAG, "Response successfully parsed.")
            }
        }
    }

    /**
     * Note the API requires that the file be mono-channel.
     */
    private fun googleSpeechApiRecognize(path: String): String {
        val base64 = Base64.encodeToString(FileInputStream(path).readBytes(), Base64.NO_WRAP)

        val body = """{
            "config": {
               "languageCode": "en-US",
               "enableWordTimeOffsets": true
            },
            "audio": { "content": """".trimIndent() + base64 + """" }
        }""".trimIndent()
        val postData: ByteArray = body.toByteArray(StandardCharsets.UTF_8)

        val url = URL("https://speech.googleapis.com/v1/speech:recognize?alt=json&key=${BuildConfig.GCLOUD_API_KEY}")

        val connection = url.openConnection() as HttpURLConnection
        connection.apply {
            requestMethod = "POST"
//            connectTimeout = 300000
            doInput = true
            doOutput = true
            setRequestProperty("charset", "utf-8")
            setRequestProperty("Content-length", postData.size.toString())
            setRequestProperty("Content-Type", "application/json")
        }

        DataOutputStream(connection.outputStream).apply {
            write(postData)
            flush()
        }

        return try {
            streamToString(BufferedReader(InputStreamReader(connection.inputStream)))
        } catch (e: IOException) {
            streamToString(BufferedReader(InputStreamReader(connection.errorStream)))
        } finally {
            connection.disconnect()
        }
    }

    private fun streamToString(reader: BufferedReader): String {
        val response = StringBuffer()

        var inputLine = reader.readLine()
        while (inputLine != null) {
            response.append(inputLine)
            inputLine = reader.readLine()
        }
        return response.toString()
    }
}