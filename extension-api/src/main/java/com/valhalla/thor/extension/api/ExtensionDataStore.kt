package com.valhalla.thor.extension.api

interface ExtensionDataStore {
    // All methods are `suspend` so the backing I/O never blocks the caller's thread.
    suspend fun saveString(key: String, value: String)
    suspend fun getString(key: String): String?
    suspend fun deleteString(key: String)
    suspend fun getAllKeys(): List<String>
}
