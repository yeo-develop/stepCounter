package com.yeo.develop.stepcounter.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.yeo.develop.stepcounter.ApplicationConstants
import com.yeo.develop.stepcounter.R
import com.yeo.develop.stepcounter.activities.MainActivity
import com.yeo.develop.stepcounter.database.steps.StepDataEntity
import com.yeo.develop.stepcounter.database.steps.reposittory.StepRepository
import com.yeo.develop.stepcounter.datastore.AppDataStore
import com.yeo.develop.stepcounter.worker.MidnightWorkerScheduler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

/**
 * 사용자의 걸음 수를 측정하고, 저장하는 서비스입니다.
 * */
@AndroidEntryPoint
class StepCounterService : Service(), SensorEventListener {

    @Inject
    lateinit var stepRepository: StepRepository

    @Inject
    lateinit var appDataStore: AppDataStore

    @Inject
    lateinit var midnightWorkerScheduler: MidnightWorkerScheduler

    private val sensorManager by lazy {
        getSystemService(SENSOR_SERVICE) as SensorManager
    }

    private val entityTimeFormatter =
        DateTimeFormatter.ofPattern(ApplicationConstants.ENTITY_DATE_FORMAT)

    /**
     * onSensorChanged 에서 콜백이 들어올때마다 값을 db를 업데이트하기엔 잦은 I/O작업으로 부하가 들것이라 판단, 5초간 적산 후 db를 갱신하는 식으로 처리합니다.
     * */
    private var accumulateStepJob: Job? = null


    /**
     * 뮤텍스를 사용하고싶었으나 sensorChanged 함수가 suspend가 아니라.. 애매하군요
     * */
    private val lock = Any()

    //5초간 들어온 값을 누적할 스토리지 변수입니다.
    private var accumulatedSteps: Int = 0
    override fun onCreate() {
        super.onCreate()
        Log.d("YDW", "Service Start!")
        createNotificationChannel()

        /**
         * 자정이 지나면 dailyStepsCount를 초기화하도록 스케쥴링합니다.
         * */
        midnightWorkerScheduler.scheduleAlarm()

        /**
         * 센서가 존재하지 않는경우엔 서비스를 종료합니다.
         * */
        sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)?.let { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST)

            accumulateStepJob = CoroutineScope(Dispatchers.IO).launch {
                while (isActive) {
                    delay(5_000L)
                    synchronized(lock) {
                        Log.d("YDW", "UPDATE! $accumulatedSteps")
                        appDataStore.dailyTotalSteps += accumulatedSteps
                        accumulatedSteps = 0
                    }
                    updateSteps(appDataStore.dailyTotalSteps)
                }
            }.apply {
                start()
            }

        } ?: {
            Toast.makeText(this, "걸음 수 계산을 지원하지 않는 디바이스 입니다.", Toast.LENGTH_SHORT).show()
            stopSelf()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = getNotification(
            context = this,
            steps = 0
        ).build()
        startForeground(1, notification)

        // 만보기 앱 특성상 앱에서 명시적인 종료가 있지 않은 이상 최대한 꺼지지 않아야한다고 생각했습니다.
        return START_STICKY
    }

    private fun createNotificationChannel() {
        val name = "Step Counter"
        val descriptionText = "Notifications for step counter"
        val channel = NotificationChannel(
            STEP_COUNTER_SERVICE_CHANNEL_ID,
            name,
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun getNotification(context: Context, steps: Int): NotificationCompat.Builder {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(context, STEP_COUNTER_SERVICE_CHANNEL_ID)
            .setContentTitle("걸음 수를 측정 하고 있어요!")
            .setContentText("현재 걸음 수: $steps")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
    }

    /**
     * 서비스의 시작 시점은 권한을 받고 난 이후입니다. 따라서 권한에대한 별도 처리를 진행하지 않습니다.
     * */
    @SuppressLint("MissingPermission")
    private fun updateNotification(steps: Int) {
        val notification = getNotification(this, steps).build()
        NotificationManagerCompat.from(this).notify(1, notification)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_STEP_DETECTOR) {
            //TYPE_STEP_DETECTOR는 걸을때마다 1을 방출합니다. 편리하군요
            synchronized(lock) {
                accumulatedSteps += event.values[0].toInt()
                Log.d(
                    "YDW",
                    "accumulate distance! curr : $accumulatedSteps, total : ${appDataStore.dailyTotalSteps}"
                )
            }
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        /** no-op **/
    }

    private suspend fun updateSteps(steps: Int) {
        updateNotification(steps)
        val today = LocalDate.now().format(entityTimeFormatter)
        stepRepository.insertOrUpdate(
            StepDataEntity(
                targetDate = today,
                steps = steps
            )
        )
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)

        accumulateStepJob?.cancel()
        accumulateStepJob = null
    }
    companion object {
        private const val STEP_COUNTER_SERVICE_CHANNEL_ID = "StepCounterServiceChannelId"
    }
}