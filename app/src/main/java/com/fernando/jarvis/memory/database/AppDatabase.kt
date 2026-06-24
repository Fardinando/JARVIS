package com.fernando.jarvis.memory.database

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase

@Entity
data class CommandHistory(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val command: String,
    val response: String,
    val timestamp: Long,
    val dateStr: String,
)

@Dao
interface HistoryDao {
    @Query("SELECT * FROM CommandHistory ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecent(limit: Int): List<CommandHistory>

    @Insert
    suspend fun insert(history: CommandHistory)
}

@Entity
data class StoredContact(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val relation: String,
    val tags: String,
)

@Dao
interface ContactDao {
    @Query("SELECT * FROM StoredContact ORDER BY name ASC")
    suspend fun getAll(): List<StoredContact>

    @Insert
    suspend fun insert(contact: StoredContact)
}

@Database(
    entities = [CommandHistory::class, StoredContact::class],
    version = 1,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao
    abstract fun contactDao(): ContactDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "jarvis_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
