
-dontobfuscate

# Resource IDs are translated back to asset names through reflection.
-keep class com.corrodinggames.rts.R$* {
    public static <fields>;
}

# Kotlin reflection is used by the mod INI loader.
-keepattributes RuntimeVisibleAnnotations,RuntimeInvisibleAnnotations,AnnotationDefault,Signature,InnerClasses,EnclosingMethod
-keep class kotlin.Metadata { *; }

# Mods implement these public entry points outside the application APK.
-keep public class io.github.rwx.mod.api.** { public protected *; }
-keep public class io.github.rwx.mod.Mod { public protected *; }
-keep public class io.github.rwx.mod.JvmMod { public protected *; }

# Keep enum names used by configuration parsing.
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Optional JVM-only backends referenced by Apache HttpClient and Netty are unavailable on Android.
-dontwarn javax.naming.**
-dontwarn org.ietf.jgss.**
-dontwarn org.apache.log4j.**
-dontwarn org.apache.logging.log4j.**
-dontwarn reactor.blockhound.integration.BlockHoundIntegration
