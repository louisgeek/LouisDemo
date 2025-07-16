#include <jni.h>
#include <string>


#include <android/log.h>

#define TAG    "zfq" // 这个是自定义的LOG的标识
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,TAG ,__VA_ARGS__)

extern "C" JNIEXPORT jstring JNICALL
Java_com_louis_myjni_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_com_louis_myjni_MainActivity_doSometing(JNIEnv *env, jobject thiz, jint int_value) {
    jfloat float_value = (jfloat) int_value;
    jfloat float_value_new = 2.6F + float_value;

    //通过 Java 实例对象 thiz 来获取 Java 类对象
    jclass clazz = env->GetObjectClass(thiz);
    jclass classClass = env->FindClass("java/lang/Class");
//    jclass classClass = env->FindClass("com/louis/myjni/MainActivity");
    jmethodID getCanonicalNameMethod = env->GetMethodID(classClass, "getCanonicalName",
                                                        "()Ljava/lang/String;");
    // Call the getCanonicalName method on the provided jclass
    jstring className = (jstring) env->CallObjectMethod(clazz, getCanonicalNameMethod);
    // Convert the returned jstring to a C-style string
    const char *canonicalName = env->GetStringUTFChars(className, NULL);

    LOGE("C 打印 onSometing 方法返回值是：%s", canonicalName);
    //通过 jclass 获取对应的函数 ID
    jmethodID methodId = env->GetMethodID(clazz, "onSometing", "(F)Ljava/lang/String;");
    //
    //通过 thiz 对象和函数 ID 来调用函数，无返回值用 CallVoidMethod，返回值 int 用 CallIntMethod
    jobject backObj = env->CallObjectMethod(thiz, methodId, float_value_new);
    jstring backStr = (jstring) (backObj);
    //
    const char *newStr = env->GetStringUTFChars(backStr, 0);
    LOGE("C 打印 onSometing 方法返回值是：%s", newStr);//C 打印 onSometing 方法返回值是：floatValue=11.6
    return true;
}

//static JNINativeMethod methods[] = {
//        {"sayHello", "(Ljava/lang/String;)Ljava/lang/String;", (void*)sayHello},
//}

//定义本地函数
jstring stringFromJNI_JNI_OnLoad(JNIEnv *env, jobject/* this */) {
    std::string hello = "Hello from C++  stringFromJNI_JNI_OnLoad";
    return env->NewStringUTF(hello.c_str());
}

//定义一个 JNINativeMethod 结构体数组，结构体包含 Java 方法名、方法签名（方法描述符）以及对应关联的本地函数的指针
JNINativeMethod nativeMethods[] = {
        {"stringFromJNI", "()Ljava/lang/String;", (jstring *) stringFromJNI_JNI_OnLoad}
};

//Java 调用 System.loadLibrary 时此 JNI_OnLoad 函数就会执行
//JavaVM *vm 参数是一个指向 Java 虚拟机的指针，通过这个指针可以访问 JVM 的功能
//void *reserved 是一个保留参数，通常不使用
JNIEXPORT jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env;
    //获取与当前线程关联的 JNIEnv 指针
    if (vm->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK) {
        LOGE("获取 JNIEnv 环境失败");
        return JNI_ERR;
    }
    //通过类的全限定名来查找并加载一个类，返回一个指向该类的 jclass 引用
    jclass clazz = env->FindClass("com/louis/myjni/MainActivity");
    if (clazz == NULL) {
        LOGE("获取 jclass 失败");
        return JNI_ERR;
    }
    //计算 arr 数组的长度 int length = sizeof(arr) / sizeof(arr[0]);
    int nMethods = sizeof(nativeMethods) / sizeof(nativeMethods[0]);
    //参数 nMethods 指定数组中本地方法的数量
    if (env->RegisterNatives(clazz, nativeMethods, nMethods) != JNI_OK) {
        LOGE("注册本地方法失败");
        return JNI_ERR;
    }
    LOGE("注册成功");
    return JNI_VERSION_1_6; //返回当前支持的 JNI 版本号
}

