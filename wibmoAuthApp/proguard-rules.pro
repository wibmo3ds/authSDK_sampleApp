
#-keep class com.wibmo.core.** { *; }


-optimizationpasses 5

#code/removal/simple,code/removal/advanced,
-optimizations !code/simplification/arithmetic

#When not preverifing in a case-insensitive filing system, such as Windows. Because this tool unpacks your processed jars, you should then use:
-dontusemixedcaseclassnames

#Specifies not to ignore non-public library classes. As of version 4.5, this is the default setting
-dontskipnonpubliclibraryclasses

#Preverification is irrelevant for the dex compiler and the Dalvik VM, so we can switch it off with the -dontpreverify option.
-dontpreverify

-keep class android.support.v4.app.** { *; }
-keep class android.support.v7.** { *; }
-dontwarn android.support.**
-dontwarn org.apache.http.**
-dontwarn android.net.http.AndroidHttpClient
-dontwarn com.google.android.gms.**
-dontwarn com.squareup.okhttp.**

# For CleverTap SDK
-dontwarn com.clevertap.android.sdk.**

# keep everything in this package from being removed or renamed
#-keep class wibmo.** { *; }

# keep everything in this package from being renamed only
#-keepnames class wibmo.** { *; }

#To remove debug logs:
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** w(...);
}

-keep class com.google.android.gms.internal.** { *; }
-dontwarn com.google.android.gms.**
-keep class com.crashlytics.** { *; }
-dontwarn com.crashlytics.**
-ignorewarnings


-dontnote com.google.vending.licensing.ILicensingService
-dontnote **ILicensingService
#-keep class * {
#    public private *;
#}

# JSR 305 annotations are for embedding nullability information.
-dontwarn javax.annotation.**

# A resource is loaded with a relative path so the package of this class must be preserved.
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# Animal Sniffer compileOnly dependency to ensure APIs are compatible with older versions of Java.
-dontwarn org.codehaus.mojo.animal_sniffer.*

# OkHttp platform used only on JVM and when Conscrypt dependency is available.
-dontwarn okhttp3.internal.platform.ConscryptPlatform

-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}


# For CleverTap SDK
-dontwarn com.clevertap.android.sdk.**


#To remove debug logs:
-assumenosideeffects class android.util.Log {
    public static *** v(...);
    public static *** d(...);
    public static *** i(...);
    public static *** w(...);
    public static *** e(...);
}


-keep class com.google.android.gms.internal.** { *; }
-dontwarn com.google.android.gms.**
-keep class com.crashlytics.** { *; }
-dontwarn com.crashlytics.**
#-ignorewarnings


-keep class com.google.android.gms.internal.** { *; }

-keep class com.wibmo.core.** { *; }
-keep class com.wibmo.Common.** { *; }
-keep class com.wibmo.core.** { *; }

-dontnote rx.internal.util.PlatformDependent
# JSR 305 annotations are for embedding nullability information.
-dontwarn javax.annotation.**

# A resource is loaded with a relative path so the package of this class must be preserved.
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# Animal Sniffer compileOnly dependency to ensure APIs are compatible with older versions of Java.
-dontwarn org.codehaus.mojo.animal_sniffer.*

# OkHttp platform used only on JVM and when Conscrypt dependency is available.
-dontwarn okhttp3.internal.platform.ConscryptPlatform
-dontwarn java.util.concurrent.**
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}



#######Wibmo Sdk Class Obfuscation #############


##---------------Begin: proguard configuration for RxJava  ----------

-keepclassmembers class rx.internal.util.unsafe.** {
    long producerIndex;
    long consumerIndex;
}

-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    long producerNode;
    long consumerNode;
}

-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}

-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}
-dontnote rx.internal.util.PlatformDependent
# JSR 305 annotations are for embedding nullability information.
-dontwarn javax.annotation.**

##---------------End: proguard configuration for RxJava  --------



-repackageclasses 'o'
