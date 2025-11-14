#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL
Java_com_flamapp_demo_MainActivity_stringFromJNI(JNIEnv* env, jobject /* this */) {
    std::string hello = "FlamApp native lib loaded";
    return env->NewStringUTF(hello.c_str());
}
