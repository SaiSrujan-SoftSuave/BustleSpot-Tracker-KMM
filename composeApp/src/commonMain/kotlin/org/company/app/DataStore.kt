//package com.softsuave.bustlespotsample
//
//import androidx.datastore.core.DataStore
//import androidx.datastore.preferences.core.PreferenceDataStoreFactory
//import androidx.datastore.preferences.core.Preferences
//import androidx.datastore.preferences.core.stringPreferencesKey
//import kotlinx.coroutines.flow.first
//import kotlinx.coroutines.flow.map
//import okio.Path.Companion.toPath
//
//fun createDataStore(productPath: () -> String): DataStore<Preferences> {
//    return PreferenceDataStoreFactory.createWithPath(produceFile = {
//        productPath().toPath()
//    })
//}
//
//internal const val DATA_STORE_FILE_NAME = "user_preferences.preferences_pb"
//
//suspend fun fetchAccessTokenFromPrefs(dataStore: DataStore<Preferences>): String {
//    return dataStore.data
//        .map { preferences ->
//            preferences[stringPreferencesKey("access_token")] ?: ""
//        }
//        .first()
//}