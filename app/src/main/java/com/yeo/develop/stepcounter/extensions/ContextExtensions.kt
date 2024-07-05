package com.yeo.develop.stepcounter.extensions

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.yeo.develop.stepcounter.ApplicationConstants


val Context.dataStore: DataStore<Preferences>
    by preferencesDataStore(name = ApplicationConstants.DATASTORE_NAME)