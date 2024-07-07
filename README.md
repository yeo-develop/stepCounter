# Step Counter App

## 개요
사용자의 걸음 수를 기록하는 앱입니다. 
Jetpack Compose를 사용하여 UI를 구성하였으며, `TedPermission` 라이브러리를 사용하여 권한을 관리합니다. 
앱은 매일 자정에 걸음 수를 초기화하고, 목표 걸음 수와 남은 걸음 수, 현재 걸은 걸음 수를 표시합니다.

## 주요 기능
- 현재 걸음 수 표시
- 목표 걸음(1000보) 표시
- 목표 달성까지 남은 걸음 수 표시
- 매일 자정 걸음 수 초기화
- 필요한 권한 요청

## 사용 기술
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [WorkManager](https://developer.android.com/jetpack/androidx/releases/work?hl=ko)
- [Room Database](https://developer.android.com/training/data-storage/room?hl=ko)
- [Hilt DI](https://developer.android.com/training/dependency-injection/hilt-android?hl=ko)
- [DataStore](https://developer.android.com/jetpack/androidx/releases/datastore?hl=ko)
- [TedPermission](https://github.com/ParkSangGwon/TedPermission)
- Kotlin

## 개발 환경
- Android Studio Giraffe
- JDK 17
- Kotlin Version 1.9.0
- gradle 8.0

## 테스트 환경
- Galaxy S24 Ultra (SDK 34)

## 계층 구조
```
com.yeo.devleop.stepcounter
├── ApplicationConstants.kt
├── MainApplication.kt
├── activities
│   ├── MainActivity.kt
│   ├── screens
│   │   └── StepCounterScreen.kt
│   └── viewmodel
│       └── StepCounterViewModel.kt
├── configs
│   └── PermissionAllowanceStatus.kt
├── database
│   └── steps
│       ├── StepDao.kt
│       ├── StepDataEntity.kt
│       └── StepDatabase.kt
├── datastore
│   └── AppDataStore.kt
├── extensions
│   └── ContextExtensions.kt
├── modules
│   ├── DataStoreModule.kt
│   └── DatabaseModule.kt
├── services
│   └── StepCounterService.kt
├── ui
│   ├── ColorPalettes.kt
│   └── Footer.kt
└── worker
    ├── MidnightWorker.kt
    ├── MidnightWorkerFactory.kt
    ├── MidnightWorkerScheduler.kt
    └── alarm
        └── AlarmReceiver.kt

```

### 주요 기능
- StepCounterService 를 이용한 걸음 값 적산
- StepsDatabase을 이용한 날짜별 거리값 저장 
- MidnightWorker를 이용해 날짜가 바뀔때마다 걸음 값 초기화
- 걸음 기록 조회 기능을 통한 걷기 히스토리 조회

### 권한
SDK 34 기준 아래와 같은 권한이 필요합니다.
  - android.permission.FOREGROUND_SERVICE : 앱이 꺼지고 나서도 지속적으로 거리값을 적산하기 위함입니다.
  - android.permission.POST_NOTIFICATIONS : Service 인스턴스를 생성할때에 유저들에게 이 서비스가 돌아가고 있다는것을 인지시켜주기 위한 Notification Channel을 생성하기 위함입니다.
  - android.permission.ACTIVITY_RECOGNITION : 센서를 통해 유저의 총 걸음거리를 적산하기 위함입니다.
  - android.permission.SCHEDULE_EXACT_ALARM : 정확한 시간에 걸음값을 초기화 하기 위함입니다.
  - android.permission.USE_EXACT_ALARM : SCHEDULE_EXACT_ALARM과 동일하게 매일 자정에 걸음 수를 초기화하는 작업을 정확히 실행하기 위해 필요합니다.
  - android.permission.WAKE_LOCK : 걸음 수 추적 또는 자정에 데이터를 초기화하는 작업이 실행될때 디바이스가 doze에 빠지지 않도록 하기 위해 필요합니다.

### 사용 예제
<p align="center">
  <img src="https://github.com/yeo-develop/stepCounter/assets/143160346/52f693c4-da67-4a51-9337-7e1e01fe9451" width="200" height="400"/>
    <p align ="center">처음 앱에 들어가게되면 만나게될 화면입니다. 첫 진입시 권한을 요구하며 거부시 정상적으로 동작하지 않습니다.</p>
</p>

<p align="center"> 
    <img src="https://github.com/yeo-develop/stepCounter/assets/143160346/18178d5a-21d0-4e84-8c61-25b9d54654a3" width="200" height="400"/>
    <p align = "center">foreground 상태에서 걸음 수 적산을 하는 모습 </p>
</p>

<p align="center"> 
    <img src="https://github.com/yeo-develop/stepCounter/assets/143160346/597390a5-664c-4f7c-b521-37b09be0f2e0" width="200" height="400"/>
    <p align = "center">background 상태에서 걸음 수 적산을 하는 모습 </p>
</p>


<p align="center"> 
    <img src="https://github.com/yeo-develop/stepCounter/assets/143160346/893cb068-e6ac-4b65-a67f-761f0a7a6351" width="200" height="400"/>
    <p align = "center">자정이 되어 걸음 값을 초기화 하는 모습 </p>
</p>


<p align="center">
  <img src="https://github.com/yeo-develop/stepCounter/assets/143160346/020b09fc-bf69-48c9-8d0c-eda418f1f3cf" width="800" height="400"/>
  <p align="center">전날 걸었던 걸음 값이 DB에 기록 된 모습.</p>
</p>

### 2024.07.07 추가 업데이트
데이터를 저장만 하고 표출을 하지 하는건 의미가 없다 판단해 걷기 기록 기능을 추가하였습니다!

<p align="center">
  <img src="https://github.com/yeo-develop/stepCounter/assets/143160346/8d89df2a-62ab-42c4-9da7-29f0c9d0595e" width="200" height="400"/>
    <img src="https://github.com/yeo-develop/stepCounter/assets/143160346/dc950985-117a-441f-98f2-bf305d106001" width="200" height="400"/>
    
  <p align="center"> 상술했던 StepCounter의 기능은 유지되며, 하단에 bottomNavigationBar가 추가 되어 있습니다.<br> 하단 바의 "걷기 기록"을 클릭해 화면을 옮길 수 있으며, 여태 로컬 DB에 적산된 걸음 값과 총 소모된 칼로리, 일별 걸음 조회를 할 수 있습니다.</p>
</p>
