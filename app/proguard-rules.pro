# Hilt
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager

# Room
-keep class androidx.room.** { *; }
-keep @androidx.room.Entity class * { *; }
-dontwarn androidx.room.**

# Compose
-keep class androidx.compose.** { *; }

# WorkManager
-keep class androidx.work.** { *; }

# Gson
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# App specific
-keep class com.dataguard.data.** { *; }
-keep class com.dataguard.domain.** { *; }
-keep class com.dataguard.service.** { *; }

# Generic
-dontwarn java.lang.invoke.*
-dontwarn **$$Lambda$*
