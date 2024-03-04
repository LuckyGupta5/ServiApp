plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("androidx.navigation.safeargs.kotlin")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")

}

android {
    namespace = "com.example.servivet"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.example.servivet"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures {
        dataBinding = true
        viewBinding = true
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")


    // networking using retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:5.0.0-alpha.2")
    implementation("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.2")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")


    // navigation
    implementation("androidx.navigation:navigation-fragment-ktx:2.6.0")
    implementation("androidx.navigation:navigation-ui-ktx:2.6.0")


    //CIRCLE IMAGE
    implementation("de.hdodenhof:circleimageview:3.1.0")

    // local storage preference
    implementation("com.orhanobut:hawk:2.0.1")

    //GLIDE TO SET IMAGE
    implementation("com.github.bumptech.glide:glide:4.13.1")
    annotationProcessor("com.github.bumptech.glide:compiler:4.12.0")


    //map
    implementation("com.google.android.gms:play-services-maps:18.2.0")

    // local storage preference
    implementation("com.fasterxml.jackson.core:jackson-databind:2.11.1")
    implementation("com.github.kirich1409:viewbindingpropertydelegate-noreflection:1.5.9")
    implementation("com.github.kirich1409:viewbindingpropertydelegate-full:1.5.9")

    // OTP Library
    implementation("io.github.chaosleung:pinview:1.4.4")

    // permission
    implementation("com.karumi:dexter:6.2.3")

    //country code picker
    implementation("com.hbb20:ccp:2.6.0")

    implementation("com.google.android.flexbox:flexbox:3.0.0")

//    implementation ("com.gdacciaro:iosdialog:1.0.3")

    implementation("com.squareup.picasso:picasso:2.8")

    // indicator
    implementation("com.github.zhpanvip:viewpagerindicator:1.2.3")


    //map
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation("com.google.android.libraries.places:places:3.2.0")
    implementation("com.google.maps.android:android-maps-utils:0.5")


    //AES Security

    implementation("org.bouncycastle:bcpkix-jdk15on:1.67")

    // implementation ("group: 'org.bouncycastle', name: 'bcprov-jdk16', version: '1.46'")


    /*IO socket*/
    implementation("io.socket:socket.io-client:2.0.0") {
        exclude(group = "org.json", module = "json")
    }

    /*Crashlytics*/
    implementation("com.google.firebase:firebase-crashlytics")
    implementation("com.google.firebase:firebase-analytics")

    /*push Notification*/
    implementation("com.google.firebase:firebase-analytics")
    implementation(platform("com.google.firebase:firebase-bom:32.7.1"))
    implementation("com.google.firebase:firebase-messaging")

    /*agora*/
    implementation("io.agora.rtc:full-sdk:4.0.1")
    implementation("commons-codec:commons-codec:1.9")

    implementation("androidx.work:work-runtime-ktx:2.7.0")


}