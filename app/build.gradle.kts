plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("com.google.devtools.ksp")
    id("androidx.navigation.safeargs.kotlin")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.foodify"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.foodify"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        buildTypes {
            val serverClientId = project.findProperty("SERVER_CLIENT_ID") as String? ?: ""

            debug {
                buildConfigField("String", "SERVER_CLIENT_ID", "\"$serverClientId\"")
            }
            release {
                buildConfigField("String", "SERVER_CLIENT_ID", "\"$serverClientId\"")
                isMinifyEnabled = false
                proguardFiles(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
                )
            }
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
        buildConfig = true
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    // Retrofit for API calls
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.gson)
    // Glide and coil for image loading
    implementation(libs.glide)
    implementation(libs.coil)
    // Room for database
    implementation(libs.androidx.room.runtime)
    implementation(libs.googleid)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)
    // Views/Fragments integration
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.ui)
    // Shimmer for loading animations
    implementation(libs.shimmer)
    // Lottie for animations
    implementation(libs.lottie)
    // Koin dependencies for Android
    implementation(libs.koin.android)
    // Koin dependencies for AndroidX Navigation
    implementation(libs.koin.androidx.navigation)
    // Firebase dependencies
    implementation(platform(libs.firebase.bom))
    // Add the dependency for the Firebase Authentication library
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation("com.google.firebase:firebase-auth")
    //
    implementation(libs.androidx.security.crypto)
    // Also add the dependency for the Google Play services library and specify its version
    implementation("com.google.android.gms:play-services-auth:21.3.0")
    // nice alert dialogs
    implementation("com.github.f0ris.sweetalert:library:1.6.2")
    implementation("com.github.Musfick:Snackify:0.1.5")
    implementation("com.pierfrancescosoffritti.androidyoutubeplayer:core:12.1.1")
    implementation("kr.co.prnd:readmore-textview:1.0.0")
    implementation("com.google.firebase:firebase-firestore-ktx")
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(kotlin("reflect"))
}