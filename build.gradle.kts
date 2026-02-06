// Top-level build file where you can add configuration options common to all subprojects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.ben.manes) apply false
    kotlin("plugin.serialization") version "2.3.0"
}
buildscript {
    repositories {
        mavenCentral()
        google()
    }

    dependencies {
        classpath("ru.tinkoff.gradle:jarjar:1.1.0")
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}