# JNI 按类名/方法名绑定（Java_com_louisgeek_myffmpeg_FFmpegUtil_*），混淆会导致符号失配
-keep class com.louisgeek.myffmpeg.FFmpegUtil { *; }

# native 层通过 GetMethodID("onProgress") 回调，方法名不能被混淆
-keep interface com.louisgeek.myffmpeg.ProgressListener { *; }
