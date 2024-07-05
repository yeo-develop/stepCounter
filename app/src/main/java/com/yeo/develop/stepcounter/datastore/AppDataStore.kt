package com.yeo.develop.stepcounter.datastore

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import com.yeo.develop.stepcounter.extensions.dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

class AppDataStore(private val context: Context) {
    var dailyTotalSteps: Int
        get() {
            return getDataSynchronously(KEY_DAILY_TOTAL_STEPS) ?: 0
        }
        set(value) {
            setDataSynchronously(KEY_DAILY_TOTAL_STEPS, value)
        }
    companion object {
        /**
         * 애플리케이션이 시작된 이후부터 현재까지의 누적 걸음 수를 계산하기 위해 사용됩니다.
         * */
        private val KEY_DAILY_TOTAL_STEPS = intPreferencesKey("KEY_DAILTY_TOTAL_STEPS")
    }


    /**
     * 아래는 suspend로 값을 가져오는게 권장되는 dataStore 특성상 항상 값을 불러올때마다 scope를 열던가 하는 등의 불편함을 해소하기위해
     * SharedPrefrence 처럼 키값을 관리할 수 있게끔 제작해둔 함수들입니다.
     * */

    private fun <T> getDataSynchronously(key: Preferences.Key<T>): T? = runBlocking {
        getDataAsynchronously(key).first()
    }

    private fun <T> getDataAsynchronously(key: Preferences.Key<T>): Flow<T?> =
        context.dataStore.data.map { preferences ->
            preferences[key]
        }
    private fun <T> setDataSynchronously(key: Preferences.Key<T>, value: T?) = runBlocking {
        setDataAsynchronously(key, value)
    }

    private suspend fun <T> setDataAsynchronously(key: Preferences.Key<T>, value: T?) =
        context.dataStore.edit { mutablePreferences ->
            if (value == null) {
                mutablePreferences.remove(key)
            } else {
                mutablePreferences[key] = value
            }
        }
}
