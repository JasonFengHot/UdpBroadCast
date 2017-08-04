#include <jni.h>
#include <string>

#include "udp_broadcast_client.h"

extern "C"
JNIEXPORT void JNICALL
Java_cn_ismartv_huibin_udpbroadcastclient_MainActivity_sendUdpBroadcast(JNIEnv *env,
                                                                        jobject instance,
                                                                        jstring target_,
                                                                        jstring message_) {
    const char *target = env->GetStringUTFChars(target_, 0);
    const char *message = env->GetStringUTFChars(message_, 0);

    // TODO
    udp_broadcast_client(target, message);

    env->ReleaseStringUTFChars(target_, target);
    env->ReleaseStringUTFChars(message_, message);
}

extern "C"
JNIEXPORT jstring JNICALL
Java_cn_ismartv_huibin_udpbroadcastclient_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";

    return env->NewStringUTF(hello.c_str());
}
