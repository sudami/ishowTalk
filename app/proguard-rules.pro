# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in E:\eclipse\sdk/tools/proguard/proguard-android.txt
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
#-keep class com.hyphenate.** {*;}
#-dontwarn  com.hyphenate.**
-dontshrink
 -dontoptimize
 -dontwarn android.webkit.WebView
 -dontwarn com.umeng.**
 -dontwarn com.tencent.weibo.sdk.**
 -keep public class javax.**
 -keep public class android.webkit.**
 -dontwarn android.support.v4.**
 -keepattributes Exceptions,InnerClasses,Signature
 -keepattributes *Annotation*
 -keepattributes SourceFile,LineNumberTable

 -keep public interface com.tencent.**
 -keep public interface com.umeng.socialize.**


 -keep public class com.umeng.socialize.* {*;}

 -keep public class com.tencent.** {*;}
 -keep class com.umeng.socialize.sensor.**
 -keep class com.umeng.socialize.handler.**

 -keep class com.tencent.** {*;}
 -dontwarn com.tencent.**
 -keep public class com.umeng.soexample.R$*{
     public static final int *;
 }
 -keep public class com.umeng.soexample.R$*{
     public static final int *;
 }
 -keep class com.tencent.open.TDialog$*
 -keep class com.tencent.open.TDialog$* {*;}
 -keep class com.tencent.open.PKDialog
 -keep class com.tencent.open.PKDialog {*;}
 -keep class com.tencent.open.PKDialog$*
 -keep class com.tencent.open.PKDialog$* {*;}

 -keep class com.sina.** {*;}
 -dontwarn com.sina.**

 -keepnames class * implements android.os.Parcelable {
     public static final ** CREATOR;
 }

 -keepattributes Signature
