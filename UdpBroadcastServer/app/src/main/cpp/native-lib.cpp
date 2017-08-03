#include <jni.h>
#include <string>

#include "udp_broadcast.h"

extern "C"
JNIEXPORT void JNICALL
Java_cn_ismartv_huibin_udpbroadcast_MyService_startUdpBroadcastServer(JNIEnv *env,
                                                                      jobject instance) {
    send_udp_broadcast();

}

extern "C"
JNIEXPORT jstring JNICALL
Java_cn_ismartv_huibin_udpbroadcast_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
