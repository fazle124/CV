# Proguard rules for CV app
-keepattributes *Annotation*
-keepclassmembers class * {
    @androidx.room.* *;
}
