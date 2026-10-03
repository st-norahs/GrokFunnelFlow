# GrokFunnelFlow ProGuard rules
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.squareup.moshi.** { *; }
-keep class com.grokfunnel.data.remote.** { *; }
-dontwarn okhttp3.**
-dontwarn retrofit2.**
