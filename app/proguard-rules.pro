# Add project specific ProGuard rules here.
-keep class de.robv.android.xposed.** { *; }
-keep class com.sleepy.lsposed.** { *; }

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**

# Kotlin
-dontwarn kotlin.**
-keepclassmembers class **$WhenMappings {
    <fields>;
}
