import org.gradle.api.JavaVersion

object Config {
    val minSdk = 21
    val compileSdk = 28
    val targetSdk = 28
    val javaVersion = JavaVersion.VERSION_1_8
}

object Versions {
    val android_gradle_plugin = "3.6.2"
    val arch_core = "2.1.0"
    val atsl_rules = "1.2.0"
    val atsl_runner = "1.2.0"
    val bubble_layout = "1.2.0"
    val cardview = "1.0.0"
    val constraint_layout = "1.1.3"
    val corbind = "1.3.1"
    val core_ktx = "1.2.0"
    val coroutines = "1.3.5"
    val crashlytics = "2.10.1"
    val dagger = "2.27"
    val dagger_assisted_injection = "0.5.2"
    val daggermock = "0.8.5"
    val dexmaker = "2.25.1"
    val espresso = "3.2.0"
    val fabric = "1.31.2"
    val facebook_screenshot_plugin = "0.12.0"
    val fast_csv = "1.0.3"
    val firebase = "16.0.6"
    val flow_preferences = "1.1.1"
    val fragment = "1.2.3"
    val glide = "4.11.0"
    val google_api_client_android = "1.30.9"
    val google_api_services_drive = "v3-rev20200306-1.30.9"
    val google_http_client_gson = "1.34.2"
    val google_services_plugin = "4.3.3"
    val grgit_plugin = "4.0.0"
    val gson = "2.8.5"
    val guava = "20.0"
    val hamcrest = "1.3"
    val jsr305 = "3.0.2"
    val junit = "4.13"
    val junit_ext = "1.1.1"
    val karumi_shot_plugin = "4.0.0"
    val kotlin = "1.3.71"
    val leakcanary = "2.2"
    val lifecycle = "2.2.0"
    val material = "1.0.0"
    val materialprogressbar = "1.6.1"
    val mockito = "3.3.3"
    val mockito_kotlin = "2.2.0"
    val mockwebserver = "3.8.1"
    val mpandroidchart = "v3.0.3"
    val multidex = "2.0.1"
    val multidex_instrumentation = "2.0.0"
    val mvrx = "1.4.0"
    val navigation = "1.0.0"
    val navigation_testing = "1.0.0-alpha08"
    val okhttp_logging_interceptor = "3.9.0"
    val paging = "2.1.2"
    val play_services_auth = "17.0.0"
    val play_services_oss_licenses = "17.0.0"
    val play_services_oss_licenses_plugin = "0.10.2"
    val preferences = "1.1.0"
    val reactive_network = "0.12.3"
    val retrofit = "2.3.0"
    val room = "2.2.5"
    val rx_android = "2.1.1"
    val rxjava2 = "2.2.19"
    val support = "1.1.0"
    val support_v4 = "1.0.0"
    val threeten_backport = "1.4.2"
    val threeten_backport_android = "1.2.3"
    val timber = "4.7.1"
    val work_manager = "2.3.3"
}

object Deps {
    val android_gradle_plugin = "com.android.tools.build:gradle:${Versions.android_gradle_plugin}"
    val arch_core_testing = "androidx.arch.core:core-testing:${Versions.arch_core}"
    val atsl_rules = "androidx.test:rules:${Versions.atsl_rules}"
    val atsl_runner = "androidx.test:runner:${Versions.atsl_runner}"
    val bubble_layout = "com.daasuu:BubbleLayout:${Versions.bubble_layout}"
    val corbind = "ru.ldralighieri.corbind:corbind:${Versions.corbind}"
    val core_ktx = "androidx.core:core-ktx:${Versions.core_ktx}"
    val coroutines_android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutines}"
    val coroutines_core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}"
    val coroutines_test = "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.coroutines}"
    val crashlytics = "com.crashlytics.sdk.android:crashlytics:${Versions.crashlytics}"
    val dagger_android = "com.google.dagger:dagger-android:${Versions.dagger}"
    val dagger_android_support = "com.google.dagger:dagger-android-support:${Versions.dagger}"
    val dagger_android_support_compiler = "com.google.dagger:dagger-android-processor:${Versions.dagger}"
    val dagger_assisted_injection_annotations = "com.squareup.inject:assisted-inject-annotations-dagger2:${Versions.dagger_assisted_injection}"
    val dagger_assisted_injection_processor = "com.squareup.inject:assisted-inject-processor-dagger2:${Versions.dagger_assisted_injection}"
    val dagger_compiler = "com.google.dagger:dagger-compiler:${Versions.dagger}"
    val dagger_runtime = "com.google.dagger:dagger:${Versions.dagger}"
    val daggermock_android = "com.github.fabioCollini.daggermock:daggermock:${Versions.daggermock}"
    val daggermock_kotlin = "com.github.fabioCollini.daggermock:daggermock-kotlin:${Versions.daggermock}"
    val deps_okhttp_logging_interceptor = "com.squareup.okhttp3:logging-interceptor:${Versions.okhttp_logging_interceptor}"
    val dexmaker = "com.linkedin.dexmaker:dexmaker-mockito:${Versions.dexmaker}"
    val espresso_contrib = "androidx.test.espresso:espresso-contrib:${Versions.espresso}"
    val espresso_core = "androidx.test.espresso:espresso-core:${Versions.espresso}"
    val espresso_intents = "androidx.test.espresso:espresso-intents:${Versions.espresso}"
    val fabric_plugin = "io.fabric.tools:gradle:${Versions.fabric}"
    val facebook_screenshot_plugin = "com.facebook.testing.screenshot:plugin:${Versions.facebook_screenshot_plugin}"
    val fast_csv = "de.siegmar:fastcsv:${Versions.fast_csv}"
    val firebase_core = "com.google.firebase:firebase-core:${Versions.firebase}"
    val flow_preferences = "com.github.tfcporciuncula:flow-preferences:${Versions.flow_preferences}"
    val fragment = "androidx.fragment:fragment:${Versions.fragment}"
    val glide = "com.github.bumptech.glide:glide:${Versions.glide}"
    val glide_compiler = "com.github.bumptech.glide:compiler:${Versions.glide}"
    val google_api_client_android = "com.google.api-client:google-api-client-android:${Versions.google_api_client_android}"
    val google_api_services_drive = "com.google.apis:google-api-services-drive:${Versions.google_api_services_drive}"
    val google_http_client_gson = "com.google.http-client:google-http-client-gson:${Versions.google_http_client_gson}"
    val google_services_plugin = "com.google.gms:google-services:${Versions.google_services_plugin}"
    val grgit_plugin = "org.ajoberstar.grgit:grgit-gradle:${Versions.grgit_plugin}"
    val gson = "com.google.code.gson:gson:${Versions.gson}"
    val guava = "com.google.guava:guava:${Versions.guava}"
    val hamcrest = "org.hamcrest:hamcrest-all:${Versions.hamcrest}"
    val jsr305 = "com.google.code.findbugs:jsr305:${Versions.jsr305}"
    val junit = "junit:junit:${Versions.junit}"
    val junit_ext = "androidx.test.ext:junit:${Versions.junit_ext}"
    val karumi_shot_plugin = "com.karumi:shot:${Versions.karumi_shot_plugin}"
    val kotlin_plugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
    val kotlin_stdlib = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlin}"
    val kotlin_test = "org.jetbrains.kotlin:kotlin-test-junit:${Versions.kotlin}"
    val leakcanary = "com.squareup.leakcanary:leakcanary-android:${Versions.leakcanary}"
    val leakcanary_instrumentation = "com.squareup.leakcanary:leakcanary-android-instrumentation:${Versions.leakcanary}"
    val lifecycle_compiler = "androidx.lifecycle:lifecycle-compiler:${Versions.lifecycle}"
    val lifecycle_extensions = "androidx.lifecycle:lifecycle-extensions:${Versions.lifecycle}"
    val lifecycle_java8 = "androidx.lifecycle:lifecycle-common-java8:${Versions.lifecycle}"
    val lifecycle_runtime = "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.lifecycle}"
    val lifecycle_viewmodel = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.lifecycle}"
    val material = "com.google.android.material:material:${Versions.material}"
    val materialprogressbar = "me.zhanghai.android.materialprogressbar:library:${Versions.materialprogressbar}"
    val mock_web_server = "com.squareup.okhttp3:mockwebserver:${Versions.mockwebserver}"
    val mockito_core = "org.mockito:mockito-core:${Versions.mockito}"
    val mockito_inline = "org.mockito:mockito-inline:${Versions.mockito}"
    val mockito_kotlin = "com.nhaarman.mockitokotlin2:mockito-kotlin:${Versions.mockito_kotlin}"
    val mpandroidchart = "com.github.PhilJay:MPAndroidChart:${Versions.mpandroidchart}"
    val multidex = "androidx.multidex:multidex:${Versions.multidex}"
    val multidex_instrumentation = "androidx.multidex:multidex-instrumentation:${Versions.multidex_instrumentation}"
    val mvrx = "com.airbnb.android:mvrx:${Versions.mvrx}"
    val navigation_fragment = "android.arch.navigation:navigation-fragment-ktx:${Versions.navigation}"
    val navigation_plugin = "android.arch.navigation:navigation-safe-args-gradle-plugin:${Versions.navigation}"
    val navigation_testing = "android.arch.navigation:navigation-testing:${Versions.navigation_testing}"
    val navigation_ui = "android.arch.navigation:navigation-ui-ktx:${Versions.navigation}"
    val paging = "androidx.paging:paging-runtime:${Versions.paging}"
    val play_services_auth = "com.google.android.gms:play-services-auth:${Versions.play_services_auth}"
    val play_services_oss_licenses = "com.google.android.gms:play-services-oss-licenses:${Versions.play_services_oss_licenses}"
    val play_services_oss_licenses_plugin = "com.google.android.gms:oss-licenses-plugin:${Versions.play_services_oss_licenses_plugin}"
    val preferences = "androidx.preference:preference:${Versions.preferences}"
    val reactive_network = "com.github.pwittchen:reactivenetwork-rx2:${Versions.reactive_network}"
    val retrofit_gson = "com.squareup.retrofit2:converter-gson:${Versions.retrofit}"
    val retrofit_mock = "com.squareup.retrofit2:retrofit-mock:${Versions.retrofit}"
    val retrofit_runtime = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
    val room_compiler = "androidx.room:room-compiler:${Versions.room}"
    val room_coroutines = "androidx.room:room-coroutines:${Versions.room}"
    val room_ktx = "androidx.room:room-ktx:${Versions.room}"
    val room_runtime = "androidx.room:room-runtime:${Versions.room}"
    val room_testing = "androidx.room:room-testing:${Versions.room}"
    val rxandroid = "io.reactivex.rxjava2:rxandroid:${Versions.rx_android}"
    val rxjava2 = "io.reactivex.rxjava2:rxjava:${Versions.rxjava2}"
    val support_annotations = "androidx.annotation:annotation:${Versions.support}"
    val support_app_compat = "androidx.appcompat:appcompat:${Versions.support}"
    val support_cardview = "androidx.cardview:cardview:${Versions.cardview}"
    val support_constraint_layout = "androidx.constraintlayout:constraintlayout:${Versions.constraint_layout}"
    val support_core_utils = "androidx.legacy:legacy-support-core-utils:${Versions.support}"
    val support_recyclerview = "androidx.recyclerview:recyclerview:${Versions.support}"
    val support_v4 = "androidx.legacy:legacy-support-v4:${Versions.support_v4}"
    val support_vector_drawable = "androidx.vectordrawable:vectordrawable:${Versions.support}"
    val threeten_backport = "org.threeten:threetenbp:${Versions.threeten_backport}"
    val threeten_backport_android = "com.jakewharton.threetenabp:threetenabp:${Versions.threeten_backport_android}"
    val timber = "com.jakewharton.timber:timber:${Versions.timber}"
    val work_manager_runtime = "androidx.work:work-runtime-ktx:${Versions.work_manager}"
    val work_manager_testing = "androidx.work:work-testing:${Versions.work_manager}"
}
