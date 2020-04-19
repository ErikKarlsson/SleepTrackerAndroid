# Sleep Tracker

## Android Development

Sleep Tracker is an app which attempts to use the latest cutting edge libraries and tools.

* Entirely written in [Kotlin](https://kotlinlang.org/)
* Uses [Kotlin Coroutines](https://kotlinlang.org/docs/reference/coroutines/coroutines-guide.html) 
* Uses many of the [Architecture Components](https://developer.android.com/topic/libraries/architecture/): Room, Paging, WorkManager, LiveData, Lifecycle and Navigation
* Uses [dagger-android](https://google.github.io/dagger/android.html) for dependency injection
* Uses [MvRx](https://github.com/airbnb/MvRx) Android framework
* Modularized - feature and library modules

## Running the tests

### Unit tests

Running all unit tests
```
./gradlew testDevDebugUnitTest
```

Running unit tests in a class
```
./gradlew :features:statistics:testDevDebug --tests net.erikkarlsson.simplesleeptracker.features.statistics.item.StatisticsItemViewModelTest
```

### Screenshot tests

Recording tests

```
./gradlew devDebugExecuteScreenshotTests -Precord
```

Executing tests

```
./gradlew devDebugExecuteScreenshotTests
```

Executing tests in a class

```
./gradlew executeScreenshotTests -Pandroid.testInstrumentationRunnerArguments.class=com.your.package.YourClassTest
```

Executing a single test

```
./gradlew executeScreenshotTests -Pandroid.testInstrumentationRunnerArguments.class=com.your.package.YourClassTest#yourTest
```

### Espresso tests

```
./gradlew connectedAndroidTest
```

## Static analysis

### Detekt

```
./gradlew detekt
```

### Lint

```
./gradlew lint
```

## Updating dependencies

```
./gradlew dependencyUpdates -Drevision=release
```

## Tasks

```
./gradlew tasks
```
