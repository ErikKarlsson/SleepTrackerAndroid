object Versions {
    val build_tools = "27.0.3"
    val compile_sdk = 27
    val min_sdk = 14
    val target_sdk = 27
    val version_code = 1
    val version_name = "1.0"

    val android_gradle_plugin = "3.2.0-alpha15"
    val arch_core = "1.1.0"
    val atsl_rules = "1.0.1"
    val atsl_runner = "1.0.1"
    val constraint_layout = "1.1.0"
    val core_ktx = "0.2"
    val crashlytics = "2.9.1"
    val dagger = "2.15"
    val daggermock = "0.8.4"
    val dexmaker = "2.16.0"
    val espresso = "3.0.1"
    val fabric = "1.25.2"
    val firebase = "12.0.0"
    val glide = "3.8.0"
    val google_services_plugin = "3.2.1"
    val grgit_plugin = "1.5.0"
    val guava = "20.0"
    val hamcrest = "1.3"
    val junit = "4.12"
    val junit5_plugin = "1.0.32"
    val kotlin = "1.2.31"
    val leakcanary = "1.5.4"
    val lifecycle = "1.1.0"
    val mockito = "2.17.0"
    val mockito_kotlin = "1.5.0"
    val mockwebserver = "3.8.1"
    val multidex = "1.0.3"
    val navigation = "1.0.0-alpha01"
    val okhttp_logging_interceptor = "3.9.0"
    val paging = "1.0.0"
    val paging_rx = "1.0.0-alpha1"
    val reactive_network = "0.12.3"
    val retrofit = "2.3.0"
    val room = "1.1.0-alpha1"
    val rx_android = "2.0.2"
    val rx_binding = "2.1.1"
    val rx_relay = "2.0.0"
    val rxjava2 = "2.1.12"
    val rxkotlin = "2.2.0"
    val support = "27.1.1"
    val threeten_backport = "1.3.6"
    val timber = "4.7.0"
    val work_manager = "1.0.0-alpha02"
}

object Deps {
    val android_gradle_plugin = "com.android.tools.build:gradle:${Versions.android_gradle_plugin}"
    val arch_core_testing = "android.arch.core:core-testing:${Versions.arch_core}"
    val atsl_rules = "com.android.support.test:rules:${Versions.atsl_rules}"
    val atsl_runner = "com.android.support.test:runner:${Versions.atsl_runner}"
    val core_ktx = "androidx.core:core-ktx:${Versions.core_ktx}"
    val crashlytics = "com.crashlytics.sdk.android:crashlytics:${Versions.crashlytics}"
    val dagger_android = "com.google.dagger:dagger-android:${Versions.dagger}"
    val dagger_android_support = "com.google.dagger:dagger-android-support:${Versions.dagger}"
    val dagger_android_support_compiler = "com.google.dagger:dagger-android-processor:${Versions.dagger}"
    val dagger_compiler = "com.google.dagger:dagger-compiler:${Versions.dagger}"
    val dagger_runtime = "com.google.dagger:dagger:${Versions.dagger}"
    val daggermock_android = "com.github.fabioCollini.daggermock:daggermock:${Versions.daggermock}"
    val daggermock_kotlin = "com.github.fabioCollini.daggermock:daggermock-kotlin:${Versions.daggermock}"
    val deps_okhttp_logging_interceptor = "com.squareup.okhttp3:logging-interceptor:${Versions.okhttp_logging_interceptor}"
    val dexmaker = "com.linkedin.dexmaker:dexmaker-mockito:${Versions.dexmaker}"
    val espresso_contrib = "com.android.support.test.espresso:espresso-contrib:${Versions.espresso}"
    val espresso_core = "com.android.support.test.espresso:espresso-core:${Versions.espresso}"
    val espresso_intents = "com.android.support.test.espresso:espresso-intents:${Versions.espresso}"
    val fabric_plugin = "io.fabric.tools:gradle:${Versions.fabric}"
    val firebase_core = "com.google.firebase:firebase-core:${Versions.firebase}"
    val glide = "com.github.bumptech.glide:glide:${Versions.glide}"
    val google_services_plugin = "com.google.gms:google-services:${Versions.google_services_plugin}"
    val grgit_plugin = "org.ajoberstar:grgit:${Versions.grgit_plugin}"
    val guava = "com.google.guava:guava:${Versions.guava}"
    val hamcrest = "org.hamcrest:hamcrest-all:${Versions.hamcrest}"
    val junit = "junit:junit:${Versions.junit}"
    val junit5_plugin = "de.mannodermaus.gradle.plugins:android-junit5:${Versions.junit5_plugin}"
    val kotlin_plugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
    val kotlin_stdlib = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlin}"
    val kotlin_test = "org.jetbrains.kotlin:kotlin-test-junit:${Versions.kotlin}"
    val leakcanary = "com.squareup.leakcanary:leakcanary-android:${Versions.leakcanary}"
    val leakcanary_no_op = "com.squareup.leakcanary:leakcanary-android-no-op:${Versions.leakcanary}"
    val lifecycle_compiler = "android.arch.lifecycle:compiler:${Versions.lifecycle}"
    val lifecycle_extensions = "android.arch.lifecycle:extensions:${Versions.lifecycle}"
    val lifecycle_java8 = "android.arch.lifecycle:common-java8:${Versions.lifecycle}"
    val lifecycle_runtime = "android.arch.lifecycle:runtime:${Versions.lifecycle}"
    val mock_web_server = "com.squareup.okhttp3:mockwebserver:${Versions.mockwebserver}"
    val mockito_core = "org.mockito:mockito-core:${Versions.mockito}"
    val mockito_inline = "org.mockito:mockito-inline:${Versions.mockito}"
    val mockito_kotlin = "com.nhaarman:mockito-kotlin:${Versions.mockito_kotlin}"
    val multidex = "com.android.support:multidex:${Versions.multidex}"
    val navigation_fragment = "android.arch.navigation:navigation-fragment-ktx:${Versions.navigation}"
    val navigation_ui = "android.arch.navigation:navigation-ui-ktx:${Versions.navigation}"
    val navigation_testing = "android.arch.navigation:navigation-testing:${Versions.navigation}"
    val navigation_plugin = "android.arch.navigation:navigation-safe-args-gradle-plugin:${Versions.navigation}"
    val paging = "android.arch.paging:runtime:${Versions.paging}"
    val paging_rx = "android.arch.paging:rxjava2:${Versions.paging_rx}"
    val reactive_network = "com.github.pwittchen:reactivenetwork-rx2:${Versions.reactive_network}"
    val retrofit_gson = "com.squareup.retrofit2:converter-gson:${Versions.retrofit}"
    val retrofit_mock = "com.squareup.retrofit2:retrofit-mock:${Versions.retrofit}"
    val retrofit_runtime = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
    val room_compiler = "android.arch.persistence.room:compiler:${Versions.room}"
    val room_runtime = "android.arch.persistence.room:runtime:${Versions.room}"
    val room_rxjava2 = "android.arch.persistence.room:rxjava2:${Versions.room}"
    val room_testing = "android.arch.persistence.room:testing:${Versions.room}"
    val rxandroid = "io.reactivex.rxjava2:rxandroid:${Versions.rx_android}"
    val rxbinding = "com.jakewharton.rxbinding2:rxbinding-kotlin:${Versions.rx_binding}"
    val rxjava2 = "io.reactivex.rxjava2:rxjava:${Versions.rxjava2}"
    val rxkotlin = "io.reactivex.rxjava2:rxkotlin:${Versions.rxkotlin}"
    val rxrelay = "com.jakewharton.rxrelay2:rxrelay:${Versions.rx_relay}"
    val support_annotations = "com.android.support:support-annotations:${Versions.support}"
    val support_app_compat = "com.android.support:appcompat-v7:${Versions.support}"
    val support_cardview = "com.android.support:cardview-v7:${Versions.support}"
    val support_constraint_layout = "com.android.support.constraint:constraint-layout:${Versions.constraint_layout}"
    val support_core_utils = "com.android.support:support-core-utils:${Versions.support}"
    val support_design = "com.android.support:design:${Versions.support}"
    val support_recyclerview = "com.android.support:recyclerview-v7:${Versions.support}"
    val support_v4 = "com.android.support:support-v4:${Versions.support}"
    val support_vector_drawable = "com.android.support:support-vector-drawable:${Versions.support}"
    val threeten_backport = "org.threeten:threetenbp:${Versions.threeten_backport}"
    val timber = "com.jakewharton.timber:timber:${Versions.timber}"
    val work_manager_runtime = "android.arch.work:work-runtime-ktx:${Versions.work_manager}"
    val work_manager_firebase = "android.arch.work:work-firebase:${Versions.work_manager}"
    val work_manager_testing = "android.arch.work:work-testing:${Versions.work_manager}"
}
