# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\Morotti\Desktop\Development\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-keep public class * extends java.lang.Exception
-printmapping mapping.txt

# Google
-keep class com.google.** {*;}
-keep class com.google.android.gms.common.api.Scope {*;}
-dontnote com.google.vending.licensing.ILicensingService
-dontnote **ILicensingService
-dontwarn android.support.**
-keep public class android.support.v7.widget.** { *; }
-keep public class android.support.v7.internal.widget.** { *; }
-keep public class android.support.v7.internal.view.menu.** { *; }

-keep public class * extends android.support.v4.view.ActionProvider {
    public <init>(android.content.Context);
}
-dontwarn android.support.design.**
-keep class android.support.design.** { *; }
-keep interface android.support.design.** { *; }
-keep public class android.support.design.R$* { *; }
-keep class android.support.v7.widget.RoundRectDrawable { *; }
-dontwarn android.support.v4.**
-keep class android.support.v4.app.** { *; }
-keep interface android.support.v4.app.** { *; }
-keep class android.support.v4.** { *; }
-dontwarn android.support.v7.**
-keep class android.support.v7.internal.** { *; }
-keep interface android.support.v7.internal.** { *; }
-keep class android.support.v7.** { *; }

-dontwarn pl.**

-keep public class com.google.android.gms.* { public *; }
-dontwarn com.google.android.gms.**
-keep class * extends com.google.api.client.json.GenericJson {
*;
}
-keep class com.google.api.services.drive.** {
*;
}
-keepattributes *Annotation*,Signature

# Parceler
-keep interface org.parceler.Parcel
-keep @org.parceler.Parcel class * { *; }
-keep class **$$Parcelable { *; }

# Eventbus
-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# Joda Time
-dontwarn org.joda.convert.**
-dontwarn org.joda.time.**
-keep class org.joda.time.** { *; }
-keep interface org.joda.time.** { *; }

# Butterknife
-keep public class * implements butterknife.Unbinder { public <init>(**, android.view.View); }
-keep class butterknife.*
-keepclasseswithmembernames class * { @butterknife.* <methods>; }
-keepclasseswithmembernames class * { @butterknife.* <fields>; }

# Feather
-keep class org.codejargon.** { *; }

# Gson
-keep class sun.misc.Unsafe { *; }
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Crashalitics
-keep class com.crashlytics.** { *; }
-dontwarn com.crashlytics.**

# App
-keep class it.emperor.intervaltraining.di.** { *; }
-keep class it.emperor.intervaltraining.models.** { *; }
-keep class it.emperor.intervaltraining.ui.credits.models.** { *; }
-keepattributes InnerClasses
-dontwarn sun.misc.Unsafe
-dontwarn com.akexorcist.roundcornerprogressbar.TextRoundCornerProgressBar