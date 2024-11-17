import java.util.Properties


plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("kotlin-kapt")
}

val properties = Properties().apply {
    load(project.rootProject.file("local.properties").inputStream())
}

android {
    namespace = "com.example.pillmate"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.pillmate"
        minSdk = 31
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "OCR_SECRET_KEY", "\"${properties["ocr_secret_key"]}\"")
        buildConfigField("String", "INVOKE_URL", "\"${properties["ocr_invoke_url"]}\"")

        buildConfigField("String", "TEMPLATE_SECRET_KEY", "\"${properties["template_secret_key"]}\"")
        buildConfigField("String", "TEMPLATE_INVOKE_URL", "\"${properties["template_invoke_url"]}\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
        dataBinding = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.recyclerview)
    val nav_version = "2.7.7"
    // BottomNavigation
    implementation("androidx.navigation:navigation-fragment-ktx:$nav_version")
    implementation("androidx.navigation:navigation-ui-ktx:$nav_version")

    // BottomSheet
    //implementation ("com.google.android.material:material:1.4.0")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")

    implementation ("com.squareup.retrofit2:converter-scalars:2.9.0")

    implementation ("com.github.bumptech.glide:glide:4.13.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.13.0")
    implementation ("ru.tinkoff.scrollingpagerindicator:scrollingpagerindicator:1.2.5")

    implementation ("com.google.android.material:material:1.10.0")

    // 알람 WorkManager하려면 필요함
    implementation ("androidx.work:work-runtime-ktx:2.8.1")

    val room_version = "2.6.1"

    implementation("androidx.room:room-runtime:$room_version")
    annotationProcessor("androidx.room:room-compiler:$room_version")

    // To use Kotlin annotation processing tool (kapt)
    kapt("androidx.room:room-compiler:$room_version")

    // optional - Kotlin Extensions and Coroutines support for Room
    implementation("androidx.room:room-ktx:$room_version")
}