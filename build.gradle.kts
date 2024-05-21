// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    repositories {
        google()
        //maven { url 'https://jitpack.io' }
        mavenCentral()
    }

    dependencies {

        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.7.6")
        /*        classpath 'com.google.gms:google-services:4.3.15'
        classpath "androidx.navigation:navigation-safe-args-gradle-plugin:2.5.3"*/
    }
}
plugins {
    id("com.android.application") version "8.4.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
    id("androidx.navigation.safeargs") version "2.4.2" apply false
    id("com.google.gms.google-services") version "4.4.0" apply false
    id("com.google.firebase.crashlytics") version "2.9.9" apply false


    // Add the dependency for the Google services Gradle plugin
   // id("com.google.gms.google-services") version "4.4.0" apply false

}