import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.ktfmt.gradle)
    id("ru.tinkoff.gradle.jarjar")
    kotlin("plugin.serialization") version "2.3.0"
    id("kotlin-parcelize")
}

val keystorePropertiesFile: File = rootProject.file("keystore.properties")

val baseVersionName: String = currentVersion.name

android {
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    if (keystorePropertiesFile.exists()) {
        val keystoreProperties = Properties()
        keystoreProperties.load(FileInputStream(keystorePropertiesFile))
        signingConfigs {
            create("githubPublish") {
                keyAlias = keystoreProperties["keyAlias"].toString()
                keyPassword = keystoreProperties["keyPassword"].toString()
                storeFile = file(keystoreProperties["storeFile"]!!)
                storePassword = keystoreProperties["storePassword"].toString()
            }
        }
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
    }

    defaultConfig {
        applicationId = "com.meashutoshhoon.dejava"
        minSdk = 26
        targetSdk = 36
        versionCode = 1

        multiDexEnabled = true

        versionName = baseVersionName
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            if (keystorePropertiesFile.exists()) {
                signingConfig = signingConfigs.getByName("githubPublish")
            }
        }
        debug {
            if (keystorePropertiesFile.exists()) {
                signingConfig = signingConfigs.getByName("githubPublish")
            }
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
        }
    }
    flavorDimensions += "publishChannel"

    productFlavors {
        create("generic") {
            dimension = "publishChannel"
            isDefault = true
        }

        create("githubPreview") {
            dimension = "publishChannel"
            resValue("string", "app_name", "DeJava Preview")
        }
    }

    lint { disable.addAll(listOf("MissingTranslation", "ExtraTranslation", "MissingQuantity")) }
    packaging {
        resources.excludes.addAll(
            listOf(
                "META-INF/LICENSE",
                "META-INF/LICENSE.txt",
                "META-INF/NOTICE.txt",
                "META-INF/MANIFEST.MF",
                "META-INF/rxjava.properties",
                "resources.arsc"
            )
        )
    }

    namespace = "com.meashutoshhoon.dejava"
}

ktfmt { kotlinLangStyle() }

kotlin { jvmToolchain(21) }

dependencies {
    implementation(libs.androidx.multidex)

    implementation(libs.kotlin.stdlib)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.preference)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.swiperefreshlayout)
    implementation(libs.androidx.cardview)

    implementation(libs.volley)

    // MMKV(ultra fast storage)
    implementation(libs.mmkv)

    // Android WorkManager
    implementation("androidx.work:work-runtime-ktx:2.8.1") {
        exclude(group = "com.google.guava", module = "listenablefuture")
    }

    implementation(libs.asm)
    implementation(files("libs/smali-2.2.1.jar"))
    implementation(files("libs/dex-ir-1.12.jar"))
    implementation(files("libs/dex-reader-1.15.jar"))
    implementation(files("libs/dex-tools-0.0.9.15.jar"))
    implementation(files("libs/dex-translator-0.0.9.15.jar"))

    implementation(libs.cfr)
    jarJar(files("libs/dx-1.16.jar"))

    implementation(files("libs/jadx-core-0.9.0.jar"))
    implementation(files("libs/dx-1.16.jar"))
    implementation(files("libs/android-5.1.jar"))

    implementation("org.slf4j:slf4j-api:2.0.17")
    implementation("org.slf4j:slf4j-simple:2.0.17")
    implementation("uk.com.robust-it:cloning:1.9.12")

    // APK Parser
    implementation("net.dongliu:apk-parser:2.6.10")

    // Fernflower
    implementation("com.github.fesh0r:fernflower:a0a8f0a8dd")

    // RxJava
    implementation("io.reactivex.rxjava2:rxandroid:2.1.1")
    implementation("io.reactivex.rxjava2:rxkotlin:2.4.0")

    // Tools
    implementation("commons-io:commons-io:2.21.0")
    implementation("com.jakewharton.timber:timber:5.0.1") // updated

    implementation("com.github.angads25:filepicker:1.1.1")
    implementation("com.davemorrissey.labs:subsampling-scale-image-view:3.10.0")
    implementation("com.scottyab:secure-preferences-lib:0.1.7")
    implementation("commons-io:commons-io:2.5") // Outdated but consistent with your toolchain
    implementation("com.jakewharton.timber:timber:5.0.1") // Updated from your old 4.7.1
    implementation("pub.devrel:easypermissions:3.0.0") // OK for runtime permissions
    implementation(fileTree("build/libs") {
        include("*.jar")
    })

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
}

jarJar {
    rules["dx-1.16.jar"] = "com.android.** xyz.codezero.android.@1"
}