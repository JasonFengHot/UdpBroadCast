#include <jni.h>
#include <string>

#include "udp_broadcast_client.h"

extern "C"
JNIEXPORT void JNICALL
Java_cn_ismartv_huibin_udpbroadcastclient_MainActivity_sendUdpBroadcast(JNIEnv *env,
                                                                        jobject instance) {

    // TODO
    udp_broadcast_client();

}

extern "C"
JNIEXPORT jstring JNICALL
Java_cn_ismartv_huibin_udpbroadcastclient_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";

    return env->NewStringUTF(hello.c_str());
}
