package com.fernando.jarvis.memory

import android.content.Context
import android.content.SharedPreferences
import com.fernando.jarvis.JARVISApp
import com.fernando.jarvis.memory.database.AppDatabase
import com.fernando.jarvis.memory.database.CommandHistory
import com.fernando.jarvis.memory.database.StoredContact
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MemoryEngine(context: Context) {

    private val db = AppDatabase.getDatabase(context)
    private val historyDao = db.historyDao()
    private val contactDao = db.contactDao()
    private val prefs: SharedPreferences = context.getSharedPreferences("jarvis_prefs", Context.MODE_PRIVATE)

    private val scope = CoroutineScope(Dispatchers.IO)
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("pt", "BR"))

    fun saveCommand(command: String, response: String) {
        scope.launch {
            historyDao.insert(
                CommandHistory(
                    command = command,
                    response = response,
                    timestamp = System.currentTimeMillis(),
                    dateStr = dateFormat.format(Date()),
                )
            )
        }
    }

    suspend fun getRecentHistory(count: Int = 5): String {
        val recent = historyDao.getRecent(count)
        return if (recent.isEmpty()) "Nenhum comando recente."
        else recent.joinToString("\n") { "${it.dateStr}: ${it.command}" }
    }

    fun savePreference(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    fun getPreference(key: String, default: String = ""): String {
        return prefs.getString(key, default) ?: default
    }

    fun savePreferenceBoolean(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }

    fun getPreferenceBoolean(key: String, default: Boolean = false): Boolean {
        return prefs.getBoolean(key, default)
    }

    fun saveContact(name: String, relation: String, tags: List<String>) {
        scope.launch {
            contactDao.insert(
                StoredContact(
                    name = name,
                    relation = relation,
                    tags = tags.joinToString(","),
                )
            )
        }
    }

    suspend fun getContacts(): List<StoredContact> = contactDao.getAll()

    fun findContact(input: String): StoredContact? {
        val lower = input.lowercase()
        return runBlockingOnIO {
            contactDao.getAll().firstOrNull { c ->
                lower.contains(c.name.lowercase()) ||
                c.tags.split(",").any { lower.contains(it.lowercase().trim()) }
            }
        }
    }

    private fun <T> runBlockingOnIO(block: suspend () -> T): T {
        return kotlinx.coroutines.runBlocking(Dispatchers.IO) { block() }
    }
}
