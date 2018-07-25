package dataAccessLayer

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.content.Context
import android.graphics.Color
import businessLayer.RecordingFlagRoom
import businessLayer.RecordingRoom
import helpers.businessLayer.TranscriptWordsRoom
import helpers.dataAccessLayer.TranscriptWordsDao
import speechClient.SpeechClient

@Database(
        version = 1,
        entities = [
            RecordingRoom::class,
            RecordingFlagRoom::class,
            TranscriptWordsRoom::class])
@TypeConverters(RoomTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    protected abstract fun _getRecordingDao(): RecordingDao
    protected abstract fun _getRecordingFlagDao(): RecordingFlagDao
    protected abstract fun _getTransciptWordsDao(): TranscriptWordsDao

    val recordingDao: RecordingDao
        get() = _getRecordingDao()
    val recordingFlagDao: RecordingFlagDao
        get() = _getRecordingFlagDao()
    val transcriptWordsDao: TranscriptWordsDao
        get() = _getTransciptWordsDao()

    companion object {
        private var INSTANCE: AppDatabase? = null
        private var DUMMY_INSTANCE: AppDatabase? = null

        //const val DUMMY_AUDIO_FILE = "/sdcard/SoundHunt/Albatross - Big Black Bear.mp3"
        const val DUMMY_AUDIO_FILE = "/sdcard/SoundHunt/Dummy_01.wav"
        private const val SEC = 1000L
        private const val MIN = 60 * SEC

        private val DUMMY_RECORDINGS = arrayOf(
                RecordingRoom(
                        id = 1,
                        name = "CS 446 L01",
                        path = DUMMY_AUDIO_FILE,
                        transcript = null,
                        created = RoomTypeConverters.ISO8601_SDF.parse("2018-05-07T11:36:00-0400"),
                        duration = 48*60*1000 + 45*1000),
                RecordingRoom(
                        id = 2,
                        name = "CS 446 L02",
                        path = DUMMY_AUDIO_FILE,
                        transcript = null,
                        created = RoomTypeConverters.ISO8601_SDF.parse("2018-05-09T11:37:56-0400"),
                        duration = 49*60*1000 + 12*1000),
                RecordingRoom(
                        id = 3,
                        name = "CS 446 L04",
                        path = DUMMY_AUDIO_FILE,
                        transcript = "hello world record to text with words",
                        created = RoomTypeConverters.ISO8601_SDF.parse("2018-05-12T11:31:32-0400"),
                        duration = 56*60*1000 + 18*1000),
                RecordingRoom(
                        id = 4,
                        name = "My Recording",
                        path = DUMMY_AUDIO_FILE,
                        transcript = null,
                        created = RoomTypeConverters.ISO8601_SDF.parse("2018-07-11T14:00:00-0400"),
                        duration = 15*60*1000 + 45*1000),
                RecordingRoom(
                        id = 5,
                        name = "My Other Recording",
                        path = DUMMY_AUDIO_FILE,
                        transcript = null,
                        created = RoomTypeConverters.ISO8601_SDF.parse("2018-07-13T13:00:00-0400"),
                        duration = 18*60*1000 + 46*1000))
        private val DUMMY_FLAGS = arrayOf(
                RecordingFlagRoom(
                        id = null,
                        recordingId = 1,
                        time = 15*SEC,
                        color = Color.BLUE,
                        label = "some label hello world"
                ),
                RecordingFlagRoom(
                        id = null,
                        recordingId = 1,
                        time = 15*SEC,
                        color = Color.BLUE,
                        label = "some label something ELSE!!!"
                ),
                RecordingFlagRoom(
                        id = null,
                        recordingId = 1,
                        time = 30*SEC,
                        color = Color.RED,
                        label = "good question"
                ),
                RecordingFlagRoom(
                        id = null,
                        recordingId = 1,
                        time = 5*MIN,
                        color = Color.RED,
                        label = "look up uml diagram"
                ),
                RecordingFlagRoom(
                        id = null,
                        recordingId = 4,
                        time = 30*SEC,
                        color = Color.RED,
                        label = "everything is out of tune"
                ),
                RecordingFlagRoom(
                        id = null,
                        recordingId = 4,
                        time = 5*MIN + 14*SEC,
                        color = Color.RED,
                        label = "this still sounds choppy"
                ),
                RecordingFlagRoom(
                        id = null,
                        recordingId = 5,
                        time = 7*MIN + 21*SEC,
                        color = Color.GREEN,
                        label = "this is sounding less choppy!!"
                )
        )

        fun getInstance(context: Context): AppDatabase? {
            if (INSTANCE != null) return INSTANCE

            synchronized(AppDatabase::class) {
                INSTANCE = Room.databaseBuilder(context.applicationContext,
                        AppDatabase::class.java, "soundhunt.db")
                        .build()
            }
            return INSTANCE
        }
        fun destroyInstance() { INSTANCE = null }

        /**
         * Get an instance of the database filled with dummy data.
         * Must be launched from a coroutine, because it adds items to the DB on DB creation.
         */
        suspend fun getDummyInstance(context: Context): AppDatabase? {
            if (DUMMY_INSTANCE != null) return DUMMY_INSTANCE

            synchronized(AppDatabase::class) {
                DUMMY_INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "soundhunt_DUMMY.db")
                        .build()
            }

            DUMMY_INSTANCE!!.recordingDao.deleteAll()
            DUMMY_INSTANCE!!.recordingDao.insert(DUMMY_RECORDINGS)
            DUMMY_INSTANCE!!.recordingFlagDao.deleteAll()
            DUMMY_INSTANCE!!.recordingFlagDao.insert(DUMMY_FLAGS)
            DUMMY_INSTANCE!!.transcriptWordsDao.deleteAll()
            DUMMY_INSTANCE!!.transcriptWordsDao.insert(SpeechClient.DUMMY_RESULT.toTranscriptWords(3))

            return DUMMY_INSTANCE
        }
        fun destroyDummyInstance() { DUMMY_INSTANCE = null }
    }
}