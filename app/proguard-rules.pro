# Roaring Trades ProGuard Rules

# Keep Solana MWA classes
-keep class com.solana.mobilewalletadapter.** { *; }
-dontwarn com.solana.mobilewalletadapter.**

# Keep data model classes
-keep class com.roaringtrades.game.model.** { *; }

# Keep wallet classes
-keep class com.roaringtrades.game.wallet.** { *; }

# Kotlin serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}

-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

-keep,includedescriptorclasses class com.roaringtrades.game.**$$serializer { *; }
-keepclassmembers class com.roaringtrades.game.** {
    *** Companion;
}
-keepclasseswithmembers class com.roaringtrades.game.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Compose - keep only runtime-essential classes (Compose handles its own obfuscation)
-dontwarn androidx.compose.**

# Security crypto (EncryptedSharedPreferences)
-keep class androidx.security.crypto.** { *; }
-dontwarn androidx.security.crypto.**

# Google Tink (used by EncryptedSharedPreferences)
-keep class com.google.crypto.tink.** { *; }
-dontwarn com.google.crypto.tink.**
-dontwarn com.google.errorprone.annotations.**

# Keep enum classes
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep sealed classes
-keep class com.roaringtrades.game.model.RandomEvent { *; }
-keep class com.roaringtrades.game.model.RandomEvent$* { *; }
-keep class com.roaringtrades.game.model.ChaseResult { *; }
-keep class com.roaringtrades.game.model.ChaseResult$* { *; }
-keep class com.roaringtrades.game.model.AmbushResult { *; }
-keep class com.roaringtrades.game.model.AmbushResult$* { *; }
-keep class com.roaringtrades.game.model.HeadlineHint { *; }
-keep class com.roaringtrades.game.model.HeadlineHint$* { *; }
