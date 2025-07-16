#include <jni.h>
#include <string>

#include <android/log.h>
#define LOG_TAG "TAG"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

extern "C" JNIEXPORT jstring JNICALL
Java_com_louis_myjni_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
//    std::string hello = "Hello from C++";
    std::string hello = "Hello from C++ 中文";
//    return env->NewStringUTF(hello.c_str());

    jstring jString = env->NewStringUTF(hello.c_str());
    //
    const char* c_str = env->GetStringUTFChars(jString, NULL);
    LOGD("test c_str: %s", c_str);  // 直接传递 const char*

    env->ReleaseStringUTFChars(jString, c_str);
    return jString;
}