# Keep the SendOtpResponse class and its members
-keepclassmembers class com.example.servivet.data.model.** {
    public *;
    protected *;
}

# Keep your model classes and their fields
-keepclassmembers class com.example.servivet.data.model.** {
    public *;
    protected *;
}

# Gson (if used)
-keep class com.google.gson.* { *; }
-keep class com.google.gson.annotations.* { *; }

# Retrofit
-keep class retrofit2.* { *; }
-keepattributes Signature

# OkHttp
-keep class okhttp3.* { *; }

# Keep Kotlin coroutine related classes
-keep class kotlinx.coroutines.* { *; }
-keep class kotlin.coroutines.* { *; }


-keepclassmembers class com.example.servivet.data.model.* { *; }

-keepclassmembers class com.example.servivet.data.model.** {
    <init>(...);
}

# Keep Gson classes and fields for serialization/deserialization
-keep class com.google.gson.** { *; }
-keep class com.example.servivet.data.model.home.response.** { *; }

# Keep all fields in the data model classes (for Gson)
-keepclassmembers class com.example.servivet.data.model.home.response.** {
    @com.google.gson.annotations.SerializedName *;
    @com.google.gson.annotations.Expose *;
}

# Additional rules to prevent warnings
-dontwarn com.google.gson.**
-dontwarn com.oracle.svm.core.annotate.Delete
-dontwarn com.oracle.svm.core.annotate.Substitute
-dontwarn com.oracle.svm.core.annotate.TargetClass
-dontwarn io.grpc.internal.DnsNameResolverProvider
-dontwarn io.grpc.internal.PickFirstLoadBalancerProvider
-dontwarn org.bouncycastle.jsse.BCSSLParameters
-dontwarn org.bouncycastle.jsse.BCSSLSocket
-dontwarn org.bouncycastle.jsse.provider.BouncyCastleJsseProvider
-dontwarn org.conscrypt.Conscrypt$Version
-dontwarn org.conscrypt.Conscrypt
-dontwarn org.openjsse.javax.net.ssl.SSLParameters
-dontwarn org.openjsse.javax.net.ssl.SSLSocket
-dontwarn org.openjsse.net.ssl.OpenJSSE
-dontwarn proguard.annotation.Keep
-dontwarn proguard.annotation.KeepClassMembers
