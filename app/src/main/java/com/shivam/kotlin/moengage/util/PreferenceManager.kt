package com.shivam.kotlin.moengage.util

import android.content.Context
import androidx.datastore.preferences.createDataStore
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.emptyPreferences
import androidx.datastore.preferences.preferencesKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class PreferenceManager @Inject constructor(@ApplicationContext context: Context) {
    private val dataStore = context.createDataStore("user_preferences");

    val preferenceFlow = dataStore.data
        .catch {
            emit(emptyPreferences())
        }
        .map { preference ->
                    //default to false
                preference[sort_order] ?: false
        }

    suspend fun updateSortOrder(sortOrder: Boolean){
        dataStore.edit {
            it[sort_order] = sortOrder
        }
    }


    //Default to false - which means show result in descending order  - latest first
    val sort_order = preferencesKey<Boolean>("sort_order");
}