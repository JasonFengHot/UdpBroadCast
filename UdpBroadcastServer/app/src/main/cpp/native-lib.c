#include <jni.h>
//
// Created by huibin on 03/08/2017.
//

#include "ismartv_log.h"

#include <stdio.h>
#include <sys/socket.h>  /* for socket(), connect(), sendto(), and recvfrom() */
#include <netinet/in.h>  /* for sockaddr_in and inet_addr() */
#include <string.h>      /* for memset() */
#include <unistd.h>      /* for close() */
#include <jni.h>

#define MAXBUF 65536


JNIEXPORT void JNICALL
Java_cn_ismartv_huibin_udpbroadcast_MyService_startUdpBroadcastServer(JNIEnv *env,
                                                                      jobject instance) {


    int sock, status, buflen;
    unsigned sinlen;
    char buffer[MAXBUF];
    struct sockaddr_in sock_in;
    int yes = 1;

    sinlen = sizeof(struct sockaddr_in);
    memset(&sock_in, 0, sinlen);

    sock = socket(PF_INET, SOCK_DGRAM, IPPROTO_UDP);

    sock_in.sin_addr.s_addr = htonl(INADDR_ANY);
    sock_in.sin_port = htons(10111);
    sock_in.sin_family = PF_INET;

    status = bind(sock, (struct sockaddr *) &sock_in, sinlen);
    LOGD("Bind Status = %d\n", status);

    status = getsockname(sock, (struct sockaddr *) &sock_in, &sinlen);
    LOGD("Sock port %d\n", htons(sock_in.sin_port));

    buflen = MAXBUF;
    status = 1;
    while (status > 0) {
        memset(buffer, 0, buflen);
        status = 0;
        status = recvfrom(sock, buffer, buflen, 0, (struct sockaddr *) &sock_in, &sinlen);

        jclass MainActivity = (*env)->FindClass(env, "cn/ismartv/huibin/udpbroadcast/MainActivity");


        jmethodID receiveBroadcastCallback = (*env)->GetStaticMethodID(
                env,
                MainActivity,
                "receiveBroadcastCallback",
                "(Ljava/lang/String;)V"
        );

        jstring jstringMessage = (*env)->NewStringUTF(env, buffer);

        (*env)->CallStaticVoidMethod(env, MainActivity, receiveBroadcastCallback, jstringMessage);


        LOGD("Receive Status = %d, Message = %s", status, buffer);
//    shutdown(sock, 2);
//    close(sock);
    }

}

