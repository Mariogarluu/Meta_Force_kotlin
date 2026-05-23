# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

## Meta-Force Kotlin app ProGuard / R8 rules
## Reglas mínimas para asegurar Compose, Hilt y Supabase.

# Mantener anotaciones de runtime necesarias para inyección y serialización.
-keepattributes *Annotation*

# Jetpack Compose: conservar clases y métodos anotados que se usan mediante reflexión.
-keep class **$Companion { *; }
-keepclassmembers class ** {
    @androidx.compose.runtime.Composable *;
}

# Hilt / DI: mantener clases generadas y anotadas.
-keep class dagger.hilt.** { *; }
-keep class * extends dagger.hilt.internal.ComponentManager { *; }
-keep class * extends dagger.hilt.internal.GeneratedComponentManager { *; }

# Supabase Kotlin client usa kotlinx.serialization: conservar serializables.
-keepclassmembers class ** {
    @kotlinx.serialization.Serializable *;
}

# R8 missing class during shrink: allow slf4j binding to be absent at runtime.
-dontwarn org.slf4j.impl.StaticLoggerBinder