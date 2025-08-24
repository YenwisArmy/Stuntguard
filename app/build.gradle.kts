import java.util.Properties

fun loadProps(name: String): Properties {
    val p = Properties()
    val f = rootProject.file(name)
    if (f.exists()) p.load(f.inputStream())
    return p
}

val keystorePropsFile = rootProject.file("keystore.properties")
val keystore = Properties().apply {
    if (keystorePropsFile.exists()) {
        load(keystorePropsFile.inputStream())
    } else {
        logger.warn("keystore.properties tidak ditemukan di root project")
    }
}

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("androidx.navigation.safeargs")
}

android {
    namespace = "id.project.stuntguard"
    compileSdk = 34

    defaultConfig {
        applicationId = "id.project.stuntguard"
        minSdk = 24
        targetSdk = 34
        versionCode = 2
        versionName = "1.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            storeFile = file(keystore["storeFile"] as String)
            storePassword = keystore["storePassword"] as String
            keyAlias = keystore["keyAlias"] as String
            keyPassword = keystore["keyPassword"] as String
            storeType = "JKS" // atau "PKCS12" sesuai hasil keytool -list di atas
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
        buildConfig = true
    }

    buildTypes {
        getByName("debug") {
            // pastikan debuggable
            isDebuggable = true

            // pisahkan identitas APK debug
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"

            // (opsional) matikan minify utk debug
            isMinifyEnabled = false
            isShrinkResources = false
            val api = loadProps("api.debug.properties")
            buildConfigField("String", "BASE_URL", "\"${api["BASE_URL"] ?: "https://be.kimstundguard.my.id"}\"")
            buildConfigField("String", "CLIENT_ID", "\"${api["CLIENT_ID"] ?: ""}\"")
            buildConfigField("int", "TIMEOUT_SEC", (api["TIMEOUT_SEC"] ?: "60").toString())
            buildConfigField("boolean", "ENABLE_LOG", "true")
        }
        getByName("release") {
            isMinifyEnabled = false
            isShrinkResources = false
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            val api = loadProps("api.release.properties")
            buildConfigField("String", "BASE_URL", "\"${api["BASE_URL"] ?: "https://be.kimstundguard.my.id"}\"")
            buildConfigField("String", "CLIENT_ID", "\"${api["CLIENT_ID"] ?: ""}\"")
            buildConfigField("int", "TIMEOUT_SEC", (api["TIMEOUT_SEC"] ?: "30").toString())
            buildConfigField("boolean", "ENABLE_LOG", "false") // bisa true sementara untuk diagnosis
        }
    }
}

dependencies {
    // ImagePicker(Camera and Gallery) :
    implementation("com.github.dhaval2404:imagepicker:2.1")

    // Datastore :
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // Glide (Online Image Loader) :
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // Retrofit (Network) :
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // ViewModel and LiveData :
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.1")

    // Navigation :
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")

    implementation("androidx.activity:activity-ktx:1.9.0")
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}