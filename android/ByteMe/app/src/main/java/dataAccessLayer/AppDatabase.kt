package dataAccessLayer

import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.TypeConverters
import android.content.Context
import businessLayer.RecordingFlagRoom
import businessLayer.RecordingRoom

@Database(
        version = 1,
        entities = [
            RecordingRoom::class,
            RecordingFlagRoom::class])
@TypeConverters(RoomTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    protected abstract fun _getRecordingDao(): RecordingDao
    protected abstract fun _getRecordingFlagDao(): RecordingFlagDao

    val recordingDao: RecordingDao
        get() = _getRecordingDao()
    val recordingFlagDao: RecordingFlagDao
        get() = _getRecordingFlagDao()

    companion object {
        private var INSTANCE: AppDatabase? = null
        private var DUMMY_INSTANCE: AppDatabase? = null

        const val DUMMY_AUDIO_FILE = "/sdcard/SoundHunt/Albatross - Big Black Bear.mp3"
        private val DUMMY_DATA = arrayOf(
                RecordingRoom(
                        id = null,
                        name = "CS 446 L01",
                        path = DUMMY_AUDIO_FILE,
                        transcript = null,
                        created = RoomTypeConverters.ISO8601_SDF.parse("2018-05-07T11:36:00-0400"),
                        duration = 48*60*1000 + 45*1000),
                RecordingRoom(
                        id = null,
                        name = "CS 446 L02",
                        path = DUMMY_AUDIO_FILE,
                        transcript = null,
                        created = RoomTypeConverters.ISO8601_SDF.parse("2018-05-09T11:37:56-0400"),
                        duration = 49*60*1000 + 12*1000),
                RecordingRoom(
                        id = null,
                        name = "CS 446 L04",
                        path = DUMMY_AUDIO_FILE,
                        transcript = null,
                        created = RoomTypeConverters.ISO8601_SDF.parse("2018-05-12T11:31:32-0400"),
                        duration = 56*60*1000 + 18*1000),
                RecordingRoom(
                        id = null,
                        name = "My Recording",
                        path = DUMMY_AUDIO_FILE,
                        transcript = null,
                        created = RoomTypeConverters.ISO8601_SDF.parse("2018-07-11T14:00:00-0400"),
                        duration = 15*60*1000 + 45*1000),
                RecordingRoom(
                        id = null,
                        name = "My Other Recording",
                        path = DUMMY_AUDIO_FILE,
                        transcript = null,
                        created = RoomTypeConverters.ISO8601_SDF.parse("2018-07-13T13:00:00-0400"),
                        duration = 18*60*1000 + 46*1000))

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
            DUMMY_INSTANCE!!.recordingDao.insert(DUMMY_DATA)

            return DUMMY_INSTANCE
        }
        fun destroyDummyInstance() { DUMMY_INSTANCE = null }
    }
}