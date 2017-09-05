# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/CodeineBot/Library/Android/sdk/tools/proguard/proguard-android.txt
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
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Facebook
-keep class com.facebook.** { *; }

# Javascript
-keepattributes JavascriptInterface
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# GMS
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**

# Newrelic
-keep class com.newrelic.** { *; }
-dontwarn com.newrelic.**
-keepattributes Exceptions, Signature, InnerClasses, LineNumberTable

# Crashlytics
-keep class com.crashlytics.** { *; }
-keep class com.crashlytics.android.**
-keepattributes SourceFile,LineNumberTable

# Braintree
-dontwarn com.devicecollector.**
-dontwarn com.braintreepayments.**

# support design
-dontwarn android.support.design.**

# AppCompat
-dontwarn android.support.v7.**

#OkHttp3
-keep class okhttp3.** { *; }
-dontwarn okhttp3.**
-dontwarn okio.**

-keep class * {
    public <fields>;
    private <fields>;
    protected <fields>;
    <fields>;
}

#RetroLambda
-dontwarn java.lang.invoke.*

#Google classes
-keep class com.google.**
-dontwarn com.google.**

#GreenDao
-keepattributes *Annotation*
-keepclassmembers class * extends org.greenrobot.greendao.AbstractDao {
    public static java.lang.String TABLENAME;
}
-keep class **$Properties

# If you do not use SQLCipher:
-dontwarn org.greenrobot.greendao.database.**
# If you do not use Rx:
-dontwarn rx.**