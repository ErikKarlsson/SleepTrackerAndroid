import org.gradle.api.JavaVersion

object Config {
    val minSdk = 21
    val compileSdk = 30
    val targetSdk = 30
    val javaVersion = JavaVersion.VERSION_1_8
}

object Versions {
    val android_gradle_plugin = "7.0.0-alpha09"
    val arch_core = "2.1.0"
    val atsl_rules = "1.2.0"
    val atsl_runner = "1.2.0"
    val bubble_layout = "1.2.0"
    val cardview = "1.0.0"
    val compose = "1.0.0-beta02"
    val constraint_layout = "2.0.0"
    val corbind = "1.3.1"
    val core_ktx = "1.2.0"
    val core_test_ktx = "1.2.0"
    val coroutines = "1.3.9"
    val crashlytics = "17.0.0-beta04"
    val hilt = "1.0.0-alpha03"
    val dagger_hilt = "2.33-beta"
    val dexmaker = "2.25.1"
    val espresso = "3.2.0"
    val facebook_screenshot_plugin = "0.13.0"
    val fast_csv = "1.0.3"
    val firebase = "16.0.6"
    val firebase_crashlytics_plugin = "2.0.0-beta04"
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
    val kotlin = "1.4.31"
    val leakcanary = "2.2"
    val lifecycle = "2.2.0"
    val material = "1.0.0"
    val materialprogressbar = "1.6.1"
    val mockito = "3.3.3"
    val mockito_kotlin = "2.2.0"
    val mpandroidchart = "v3.0.3"
    val multidex = "2.0.1"
    val multidex_instrumentation = "2.0.0"
    val navigation = "1.0.0"
    val navigation_testing = "1.0.0-alpha08"
    val paging = "2.1.2"
    val play_services_auth = "17.0.0"
    val play_services_oss_licenses = "17.0.0"
    val play_services_oss_licenses_plugin = "0.10.2"
    val preferences = "1.1.0"
    val room = "2.2.5"
    val support = "1.1.0"
    val support_app_compat = "1.3.0-beta01"
    val support_v4 = "1.0.0"
    val threeten_backport = "1.4.2"
    val threeten_backport_android = "1.2.3"
    val timber = "4.7.1"
    val work_manager = "2.4.0"
}

object Deps {
    val android_gradle_plugin = "com.android.tools.build:gradle:${Versions.android_gradle_plugin}"
    val arch_core_testing = "androidx.arch.core:core-testing:${Versions.arch_core}"
    val atsl_rules = "androidx.test:rules:${Versions.atsl_rules}"
    val atsl_runner = "androidx.test:runner:${Versions.atsl_runner}"
    val bubble_layout = "com.daasuu:BubbleLayout:${Versions.bubble_layout}"
    val compose_ui = "androidx.compose.ui:ui:${Versions.compose}"
    val compose_ui_tooling = "androidx.compose.ui:ui-tooling:${Versions.compose}"
    val compose_foundation = "androidx.compose.foundation:foundation:${Versions.compose}"
    val compose_material = "androidx.compose.material:material:${Versions.compose}"
    val compose_material_icons = "androidx.compose.material:material-icons-core:${Versions.compose}"
    val compose_material_icons_extended = "androidx.compose.material:material-icons-extended:${Versions.compose}"
    val compose_livedata = "androidx.compose.runtime:runtime-livedata:${Versions.compose}"
    val compose_ui_test = "androidx.ui:ui-test:${Versions.compose}"
    val corbind = "ru.ldralighieri.corbind:corbind:${Versions.corbind}"
    val core_ktx = "androidx.core:core-ktx:${Versions.core_ktx}"
    val core_test_ktx = "androidx.test:core-ktx:${Versions.core_test_ktx}"
    val coroutines_android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutines}"
    val coroutines_core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}"
    val coroutines_test = "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.coroutines}"
    val crashlytics = "com.google.firebase:firebase-crashlytics:${Versions.crashlytics}"
    val dagger_hilt = "com.google.dagger:hilt-android:${Versions.dagger_hilt}"
    val dagger_hilt_compiler = "com.google.dagger:hilt-android-compiler:${Versions.dagger_hilt}"
    val dagger_hilt_testing = "com.google.dagger:hilt-android-testing:${Versions.dagger_hilt}"
    val dagger_hilt_testing_compiler = "com.google.dagger:hilt-android-compiler:${Versions.dagger_hilt}"
    val dagger_hilt_gradle_plugin = "com.google.dagger:hilt-android-gradle-plugin:${Versions.dagger_hilt}"
    val hilt_compiler = "androidx.hilt:hilt-compiler:${Versions.hilt}"
    val hilt_viewmodel = "androidx.hilt:hilt-lifecycle-viewmodel:${Versions.hilt}"
    val hilt_work = "androidx.hilt:hilt-work:${Versions.hilt}"
    val dexmaker = "com.linkedin.dexmaker:dexmaker-mockito:${Versions.dexmaker}"
    val espresso_contrib = "androidx.test.espresso:espresso-contrib:${Versions.espresso}"
    val espresso_core = "androidx.test.espresso:espresso-core:${Versions.espresso}"
    val espresso_intents = "androidx.test.espresso:espresso-intents:${Versions.espresso}"
    val facebook_screenshot_plugin = "com.facebook.testing.screenshot:plugin:${Versions.facebook_screenshot_plugin}"
    val fast_csv = "de.siegmar:fastcsv:${Versions.fast_csv}"
    val firebase_crashlytics_plugin = "com.google.firebase:firebase-crashlytics-gradle:${Versions.firebase_crashlytics_plugin}"
    val firebase_core = "com.google.firebase:firebase-core:${Versions.firebase}"
    val flow_preferences = "com.github.tfcporciuncula:flow-preferences:${Versions.flow_preferences}"
    val fragment = "androidx.fragment:fragment:${Versions.fragment}"
    val fragment_ktx = "androidx.fragment:fragment-ktx:${Versions.fragment}"
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
    val junit_ktx = "androidx.test.ext:junit-ktx:${Versions.junit_ext}"
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
    val lifecycle_livedata = "androidx.lifecycle:lifecycle-livedata-ktx:${Versions.lifecycle}"
    val lifecycle_viewmodel = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.lifecycle}"
    val material = "com.google.android.material:material:${Versions.material}"
    val materialprogressbar = "me.zhanghai.android.materialprogressbar:library:${Versions.materialprogressbar}"
    val mockito_core = "org.mockito:mockito-core:${Versions.mockito}"
    val mockito_inline = "org.mockito:mockito-inline:${Versions.mockito}"
    val mockito_kotlin = "com.nhaarman.mockitokotlin2:mockito-kotlin:${Versions.mockito_kotlin}"
    val mpandroidchart = "com.github.PhilJay:MPAndroidChart:${Versions.mpandroidchart}"
    val multidex = "androidx.multidex:multidex:${Versions.multidex}"
    val multidex_instrumentation = "androidx.multidex:multidex-instrumentation:${Versions.multidex_instrumentation}"
    val navigation_fragment = "android.arch.navigation:navigation-fragment-ktx:${Versions.navigation}"
    val navigation_plugin = "android.arch.navigation:navigation-safe-args-gradle-plugin:${Versions.navigation}"
    val navigation_testing = "android.arch.navigation:navigation-testing:${Versions.navigation_testing}"
    val navigation_ui = "android.arch.navigation:navigation-ui-ktx:${Versions.navigation}"
    val paging = "androidx.paging:paging-runtime:${Versions.paging}"
    val play_services_auth = "com.google.android.gms:play-services-auth:${Versions.play_services_auth}"
    val play_services_oss_licenses = "com.google.android.gms:play-services-oss-licenses:${Versions.play_services_oss_licenses}"
    val play_services_oss_licenses_plugin = "com.google.android.gms:oss-licenses-plugin:${Versions.play_services_oss_licenses_plugin}"
    val preferences = "androidx.preference:preference:${Versions.preferences}"
    val room_compiler = "androidx.room:room-compiler:${Versions.room}"
    val room_ktx = "androidx.room:room-ktx:${Versions.room}"
    val room_runtime = "androidx.room:room-runtime:${Versions.room}"
    val room_testing = "androidx.room:room-testing:${Versions.room}"
    val support_annotations = "androidx.annotation:annotation:${Versions.support}"
    val support_app_compat = "androidx.appcompat:appcompat:${Versions.support_app_compat}"
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
