package com.sahrul.roomwordsample

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class WordRoomDatabase {
    // Annotates class to be a Room Database with a table (entity) of the Word class
    @Database(entities = [Word::class], version = 1)
    abstract class WordRoomDatabase : RoomDatabase() {

        abstract fun wordDao(): WordDao

        private class WordDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {

            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch {
                        var wordDao = database.wordDao()

                        // Delete all content here.
                        wordDao.deleteAll()

                        // Add sample words.
                        var word = Word("Hello")
                        wordDao.insert(word)
                        word = Word("World!")
                        wordDao.insert(word)

                        // TODO: Add your own words!
                        word = Word("TODO!")
                        wordDao.insert(word)
                    }
                }
            }
        }

        companion object {
            @Volatile
            private var INSTANCE: WordRoomDatabase? = null

            fun getDatabase(
                context: Context,
                scope: CoroutineScope
            ): WordRoomDatabase {
                // if the INSTANCE is not null, then return it,
                // if it is, then create the database
                return INSTANCE ?: synchronized(this) {
                    val instance = Room.databaseBuilder(
                        context.applicationContext,
                        WordRoomDatabase::class.java,
                        "word_database"
                    )
                        .addCallback(WordDatabaseCallback(scope))
                        .build()
                    INSTANCE = instance
                    // return instance
                    instance
                }
            }
        }
    }
}